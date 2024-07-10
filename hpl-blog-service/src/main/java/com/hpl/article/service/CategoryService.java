package com.hpl.article.service;


import com.hpl.article.dto.CategoryDTO;
import com.hpl.article.entity.Category;

import java.util.List;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface CategoryService {

    /**
     * 查询类目名
     *
     * @param categoryId
     * @return
     */
    String queryCategoryName(Long categoryId);


    /**
     * 查询所有的分离
     *
     * @return
     */
    List<CategoryDTO> loadAllCategories();

    /**
     * 刷新缓存
     */
    public void refreshCache();

    /**
     * 将Category实体转换为CategoryDTO数据传输对象。
     */
    CategoryDTO categoryToDto(Category category);

    /**
     * 查询类目id
     *
     * @param category
     * @return
     */
    Long queryCategoryId(String category);



}
