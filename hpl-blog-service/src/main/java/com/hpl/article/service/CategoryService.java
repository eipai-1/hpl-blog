package com.hpl.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto.CategoryTreeDTO;
import com.hpl.article.pojo.entity.Category;

import java.util.List;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface CategoryService extends IService<Category> {

    /**
     * 树型分类查询
     * @param id 根节点id
     * @return 根节点下面的所有子节点
     */
    List<CategoryTreeDTO> getTreeCategories(String id);

    /**
     * 根据某分类获取叶子节点
     *
     * @param categoryTreeDTO
     */
    List<String> getLeafIds(CategoryTreeDTO categoryTreeDTO);

    List<Category> getAllLeafs();
}
