package com.hpl.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.entity.Category;
import com.hpl.article.pojo.dto.CategoryDTO;

import java.util.List;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface CategoryService extends IService<Category> {

    /**
     * 查询所有的分类
     *
     * @return
     */
    List<CategoryDTO> getAllCategories();

    /**
     * 根据id查询分类目名
     *
     * @param categoryId
     * @return
     */
    String getNameById(Long categoryId);

    /**
     * 查询类目id
     *
     * @param category
     * @return
     */
    Long getIdByName(String category);

}
