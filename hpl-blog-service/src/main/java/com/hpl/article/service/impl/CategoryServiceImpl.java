package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.article.pojo.entity.Category;
import com.hpl.article.pojo.enums.PublishStatusEnum;
import com.hpl.article.mapper.CategoryMapper;
import com.hpl.article.pojo.dto.CategoryDTO;
import com.hpl.article.service.CategoryService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.redis.RedisClient;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;


/**
 * 类目Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {


    @Resource
    private CategoryMapper categoryMapper;

    @Resource
    private RedisClient redisClient;

    private final String  CATEGORY_CACHE_KEY = "category:all";

//    /**
//     * 初始化分类缓存。
//     * 使用Guava Cache构建器配置缓存，最大大小为300。当缓存达到最大大小时，最旧的条目将被移除。
//     * 缓存的目的是为了提高分类数据的访问速度，避免频繁地对数据库进行读取操作。
//     *
//     * @PostConstruct 注解表示该方法在实例初始化后调用，确保在任何方法调用之前完成缓存的初始化。
//     */
//    @PostConstruct
//    public void init() {
//        categoryCaches = CacheBuilder.newBuilder().maximumSize(300).build(new CacheLoader<Long, CategoryDTO>() {
//            /**
//             * 当缓存中不存在指定的分类ID时，该方法被调用以加载数据。
//             * 它通过查询数据库来获取分类信息，并将其转换为CategoryDTO对象存储在缓存中。
//             * 如果分类不存在或已被标记为删除，则返回一个空的CategoryDTO对象。
//             */
//            @Override
//            public @NotNull CategoryDTO load(@NotNull Long categoryId) throws Exception {
//                LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
//                wrapper.eq(Category::getId, categoryId)
//                        .eq(Category::getDeleted, CommonDeletedEnum.NO.getCode());
//                Category category = categoryMapper.selectOne(wrapper);
//                if (category == null || category.getDeleted() == CommonDeletedEnum.YES.getCode()) {
////                    return CategoryDTO.EMPTY;
//                    return CategoryDTO.builder()
//                            .categoryId(-1L)
//                            .categoryName("illegal")
//                            .build();
//                }
////                return new CategoryDTO(categoryId, category.getCategoryName(), category.getRank());
//                return CategoryDTO.builder()
//                        .categoryId(category.getId())
//                        .categoryName(category.getCategoryName())
//                        .rank(category.getRank())
//                        .build();
//            }
//        });
//    }


    /**
     * 加载所有分类信息。
     * 本方法首先检查分类缓存是否足够最新，如果缓存中的分类数量少于或等于5个，则刷新缓存。
     * 然后从缓存中获取所有分类，移除无效的分类（分类ID小于等于0），并根据分类的排名进行排序。
     * 最后返回排序后的有效分类列表。
     *
     * @return 包含所有有效分类的列表，列表中的分类按排名升序排列。
     */
    @Override
    public List<CategoryDTO> getAllCategories() {

        // redis
        List<CategoryDTO> res = redisClient.getList(CATEGORY_CACHE_KEY, CategoryDTO.class);
        if (res!=null) {
            return res;
        }

        // 构建查询条件，只查询未删除且在线状态的分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Category::getStatus, PublishStatusEnum.PUBLISHED.getCode())
                .orderByAsc(Category::getRank);

        // 根据查询条件查询符合条件的分类列表
        List<Category> list = categoryMapper.selectList(wrapper);

        // 将查询到的分类数据转换为DTO格式，并存入缓存
        List<CategoryDTO> result =  new ArrayList<>();
        list.forEach(s -> result.add(categoryToDTO(s)));
        redisClient.set(CATEGORY_CACHE_KEY, result,1L, TimeUnit.DAYS);

        return result;
    }

    /**
     * 将Category实体转换为CategoryDTO数据传输对象。
     */

    private CategoryDTO categoryToDTO(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategoryName(category.getCategoryName());
        dto.setCategoryId(category.getId());
        dto.setRank(category.getRank());
        dto.setStatus(category.getStatus());
        return dto;
    }


    /**
     * 通过类目ID查询类目名称。
     *
     * @param categoryId 类目ID，用于唯一标识一个类目。
     * @return 类目名称，根据给定的类目ID从缓存中检索。
     */
    @Override
    public String getNameById(Long categoryId) {
        // 从缓存中获取类目对象，并返回其名称
        return this.getAllCategories().stream()
                .filter(s -> s.getCategoryId().equals(categoryId))
                .findFirst()
                .map(CategoryDTO::getCategoryName)
                .orElse(null);
//                .ifPresent(s -> redisClient.set("category-name-" + categoryId, s, 1L, TimeUnit.DAYS));
    }

    /**
     * 根据类别名称查询类别ID。
     * 该方法通过遍历缓存中的所有CategoryDTO对象，找到与给定类别名称匹配的CategoryDTO，
     * 并返回其类别ID。如果找不到匹配的CategoryDTO，则返回null。
     *
     * @param category 类别名称，不区分大小写。
     * @return 匹配类别名称的CategoryDTO的类别ID，如果找不到则为null。
     */
    @Override
    public Long getIdByName(String category) {
        return this.getAllCategories().stream()
                .filter(s -> s.getCategoryName().equalsIgnoreCase(category))
                .findFirst()
                .map(CategoryDTO::getCategoryId)
                .orElse(null);
    }

}
