package com.hpl.article.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hpl.article.dto.CategoryDTO;
import com.hpl.article.dto.CategoryPostDTO;
import com.hpl.article.dto.SearchCategoryDTO;
import com.hpl.article.entity.Category;
import com.hpl.article.mapper.CategoryMapper;
import com.hpl.article.service.CategoryService;
import com.hpl.article.service.CategorySettingService;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;
import com.hpl.util.NumUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 分类后台接口
 *
 * @author louzai
 * @date 2022-09-17
 */
@Service
public class CategorySettingServiceImpl implements CategorySettingService {

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private CategoryService categoryService;

    private Category getById(Integer categoryId) {
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getId, categoryId)
                .eq(Category::getDeleted, CommonDeletedEnum.NO.getCode());

        return categoryMapper.selectOne(wrapper);
    }

    /**
     * 保存分类信息。
     * 根据传入的CategoryPostDTO对象，如果categoryId为0，则插入一个新的分类；
     * 否则，更新已存在的分类信息。完成后，刷新分类缓存。
     *
     * @param categoryPostDTO 分类信息DTO，包含分类名称、排名和分类ID。
     */
    @Override
    public void saveCategory(CategoryPostDTO categoryPostDTO) {
        // 初始化Category实体对象
        Category category = new Category();
        // 如果categoryPostDTO不为空，填充Category实体对象的属性
        if (categoryPostDTO != null) {
            category.setCategoryName(categoryPostDTO.getCategory());
            category.setRank(categoryPostDTO.getRank());
        }

        // 判断categoryId是否为0，决定是插入还是更新分类
        if (NumUtil.eqZero(categoryPostDTO.getCategoryId())) {
            categoryMapper.insert(category);
        } else {
            category.setId(categoryPostDTO.getCategoryId());
            categoryMapper.updateById(category);
        }
        // 更新完成后，刷新分类缓存
        categoryService.refreshCache();
    }


    @Override
    public void deleteCategory(Integer categoryId) {
        Category category = this.getById(categoryId);
        if (category != null){
            categoryMapper.deleteById(category);
        }
        categoryService.refreshCache();
    }


    /**
     * 根据categoryId和pushStatus操作类别。
     * 此方法主要用于更新指定类别ID的状态，并在更新后刷新缓存。
     *
     * @param categoryId 类别ID，用于定位要操作的类别。
     * @param pushStatus 推送状态，用于更新类别的推送状态。
     */
    @Override
    public void operateCategory(Integer categoryId, Integer pushStatus) {
        // 根据categoryId获取类别对象
        Category category = this.getById(categoryId);
        // 检查类别是否存在，如果存在则进行状态更新
        if (category != null){
            // 更新类别的推送状态
            category.setStatus(pushStatus);
            // 根据新的状态更新数据库中的类别信息
            categoryMapper.updateById(category);
        }
        // 更新完成后，刷新缓存以保持数据一致性
        categoryService.refreshCache();
    }

    /**
     * 根据搜索条件获取分类列表。
     *
     * @param searchCategoryDTO 搜索条件，包含分类名称、页码和每页数量等信息。
     * @return 返回分类的分页结果，包含分类DTO列表和总记录数等信息。
     */
    @Override
    public CommonPageVo<CategoryDTO> getCategoryList(SearchCategoryDTO searchCategoryDTO) {
        // 检查搜索条件是否为空，为空则直接返回null
        if (searchCategoryDTO == null) {
            return null;
        }

        // 构建查询条件，只查询未删除的分类，按更新时间降序、排名升序排序
        LambdaQueryWrapper<Category> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Category::getDeleted, CommonDeletedEnum.NO.getCode())
                .like(StringUtils.isNotBlank(searchCategoryDTO.getCategory()), Category::getCategoryName, searchCategoryDTO.getCategory())
                .orderByDesc(Category::getUpdateTime)
                .orderByAsc(Category::getRank)
                .last(CommonPageParam.getLimitSql(
                        CommonPageParam.newInstance(searchCategoryDTO.getPageNumber(), searchCategoryDTO.getPageSize())));

        // 根据查询条件获取分类列表
        List<Category> list = categoryMapper.selectList(wrapper);

        // 将分类实体转换为分类DTO列表
        // 将查询到的分类数据转换为DTO格式
        List<CategoryDTO> categoryDTOS = new ArrayList<>();
        list.forEach(s -> categoryDTOS.add(categoryService.categoryToDto(s)));

        // 构建查询条件，用于计算总记录数（与获取列表的查询条件相同）
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Category::getDeleted, CommonDeletedEnum.NO.getCode())
                .like(StringUtils.isNotBlank(searchCategoryDTO.getCategory()), Category::getCategoryName, searchCategoryDTO.getCategory());

        // 计算符合条件的总记录数
        Long totalCount = categoryMapper.selectCount(queryWrapper);

        // 构建并返回分类的分页结果
        return CommonPageVo.build(categoryDTOS, searchCategoryDTO.getPageSize(), searchCategoryDTO.getPageNumber(), totalCount);
    }

}
