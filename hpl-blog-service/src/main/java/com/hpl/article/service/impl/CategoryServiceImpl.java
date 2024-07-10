package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.common.cache.CacheBuilder;

import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.hpl.article.dto.CategoryDTO;
import com.hpl.article.entity.Category;
import com.hpl.article.enums.PushStatusEnum;
import com.hpl.article.mapper.CategoryMapper;
import com.hpl.article.service.CategoryService;
import com.hpl.pojo.CommonDeletedEnum;
import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * 类目Service
 *
 * @author louzai
 * @date 2022-07-20
 */
@Service
public class CategoryServiceImpl implements CategoryService {
    /**
     * 分类数一般不会特别多，如编程领域可以预期的分类将不会超过30，所以可以做一个全量的内存缓存
     * todo 后续可改为Guava -> Redis
     */
    private LoadingCache<Long, CategoryDTO> categoryCaches;

    private CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryDao) {
        this.categoryMapper = categoryDao;
    }


    /**
     * 初始化分类缓存。
     * 使用Guava Cache构建器配置缓存，最大大小为300。当缓存达到最大大小时，最旧的条目将被移除。
     * 缓存的目的是为了提高分类数据的访问速度，避免频繁地对数据库进行读取操作。
     *
     * @PostConstruct 注解表示该方法在实例初始化后调用，确保在任何方法调用之前完成缓存的初始化。
     */
    @PostConstruct
    public void init() {
        categoryCaches = CacheBuilder.newBuilder().maximumSize(300).build(new CacheLoader<Long, CategoryDTO>() {
            /**
             * 当缓存中不存在指定的分类ID时，该方法被调用以加载数据。
             * 它通过查询数据库来获取分类信息，并将其转换为CategoryDTO对象存储在缓存中。
             * 如果分类不存在或已被标记为删除，则返回一个空的CategoryDTO对象。
             */
            @Override
            public CategoryDTO load(@NotNull Long categoryId) throws Exception {
                LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(Category::getId, categoryId)
                        .eq(Category::getDeleted, CommonDeletedEnum.NO.getCode());
                Category category = categoryMapper.selectOne(wrapper);
                if (category == null || category.getDeleted() == CommonDeletedEnum.YES.getCode()) {
                    return CategoryDTO.EMPTY;
                }
                return new CategoryDTO(categoryId, category.getCategoryName(), category.getRank());
            }
        });
    }


    /**
     * 通过类目ID查询类目名称。
     *
     * @param categoryId 类目ID，用于唯一标识一个类目。
     * @return 类目名称，根据给定的类目ID从缓存中检索。
     */
    @Override
    public String queryCategoryName(Long categoryId) {
        // 从缓存中获取类目对象，并返回其名称
        return categoryCaches.getUnchecked(categoryId).getCategory();
    }


    /**
     * 加载所有分类信息。
     * 本方法首先检查分类缓存是否足够最新，如果缓存中的分类数量少于或等于5个，则刷新缓存。
     * 然后从缓存中获取所有分类，移除无效的分类（分类ID小于等于0），并根据分类的排名进行排序。
     * 最后返回排序后的有效分类列表。
     *
     * @return 包含所有有效分类的列表，列表中的分类按排名升序排列。
     */
    @Override
    public List<CategoryDTO> loadAllCategories() {
        // 检查缓存中的分类数量，如果不足，则刷新缓存
        if (categoryCaches.size() <= 5) {
            refreshCache();
        }

        // 从缓存中获取所有分类，并转换为List形式
        List<CategoryDTO> list = new ArrayList<>(categoryCaches.asMap().values());

        // 移除无效的分类（分类ID小于等于0）
        list.removeIf(s -> s.getCategoryId() <= 0);

        // 根据分类的排名进行排序
        list.sort(Comparator.comparingInt(CategoryDTO::getRank));

        return list;
    }


    /**
     * 刷新缓存方法，用于更新缓存中的分类数据。
     * 此方法首先根据删除状态和推送状态查询有效的分类数据，
     * 然后清除现有的分类缓存，最后将查询到的分类数据重新存入缓存。
     * 这样做的目的是为了确保缓存中的分类数据与数据库中的数据保持同步，
     * 以提供准确的数据服务。
     */
    @Override
    public void refreshCache() {
        // 构建查询条件，只查询未删除且在线状态的分类
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getDeleted, CommonDeletedEnum.NO.getCode())
                .eq(Category::getStatus, PushStatusEnum.ONLINE.getCode());

        // 根据查询条件查询符合条件的分类列表
        List<Category> list = categoryMapper.selectList(wrapper);

        // 清除缓存中的所有分类数据
        categoryCaches.invalidateAll();
        // 进一步清理缓存，确保缓存数据的准确性
        categoryCaches.cleanUp();
        // 将查询到的分类数据转换为DTO格式，并存入缓存
        list.forEach(s -> categoryCaches.put(s.getId(), categoryToDto(s)));
    }


    /**
     * 将Category实体转换为CategoryDTO数据传输对象。
     */
    @Override
    public CategoryDTO categoryToDto(Category category) {
        CategoryDTO dto = new CategoryDTO();
        dto.setCategory(category.getCategoryName());
        dto.setCategoryId(category.getId());
        dto.setRank(category.getRank());
        dto.setStatus(category.getStatus());
        dto.setSelected(false);
        return dto;
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
    public Long queryCategoryId(String category) {
        // 从categoryCaches的值流中过滤出类别名称与输入参数category忽略大小写相等的CategoryDTO
        return categoryCaches.asMap().values().stream()
                .filter(s -> s.getCategory().equalsIgnoreCase(category))
                // 找到第一个匹配的CategoryDTO
                .findFirst()
                // 提取匹配CategoryDTO的类别ID
                .map(CategoryDTO::getCategoryId)
                // 如果没有找到匹配的CategoryDTO，则返回null
                .orElse(null);
    }

}
