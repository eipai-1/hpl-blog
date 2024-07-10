package com.hpl.article.service;


import com.hpl.article.dto.CategoryDTO;
import com.hpl.article.dto.CategoryPostDTO;
import com.hpl.article.dto.SearchCategoryDTO;
import com.hpl.pojo.CommonPageVo;

/**
 * 分类后台接口
 *
 * @author louzai
 * @date 2022-09-17
 */
public interface CategorySettingService {

    void saveCategory(CategoryPostDTO categoryPostDTO);

    void deleteCategory(Integer categoryId);

    void operateCategory(Integer categoryId, Integer pushStatus);

    /**
     * 获取category列表
     * @param searchCategoryDTO
     * @return
     */
    CommonPageVo<CategoryDTO> getCategoryList(SearchCategoryDTO searchCategoryDTO);
}
