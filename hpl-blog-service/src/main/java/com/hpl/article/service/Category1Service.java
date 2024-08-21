package com.hpl.article.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto.Category1TreeDTO;
import com.hpl.article.pojo.entity.Category1;

import java.util.List;

/**
 * 标签Service
 *
 * @author louzai
 * @date 2022-07-20
 */
public interface Category1Service extends IService<Category1> {

    /**
     * 树型分类查询
     * @param id 根节点id
     * @return 根节点下面的所有子节点
     */
    List<Category1TreeDTO> getTreeCategories(String id);

    /**
     * 根据某分类获取叶子节点
     *
     * @param category1TreeDTO
     */
    List<String> getLeafIds(Category1TreeDTO category1TreeDTO);
}
