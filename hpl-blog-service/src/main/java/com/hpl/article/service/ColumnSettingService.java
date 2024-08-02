package com.hpl.article.service;



import com.hpl.article.pojo.dto1.*;
import com.hpl.pojo.CommonPageVo;

import java.util.List;

/**
 * 专栏后台接口
 *
 * @author louzai
 * @date 2022-09-19
 */
public interface ColumnSettingService {


    /**
     * 保存专栏
     *
     * @param columnPostDTO
     */
    void saveColumn(ColumnPostDTO columnPostDTO);

    /**
     * 保存专栏文章
     *
     * @param columnArticlePostDTO
     */
    void saveColumnArticle(ColumnArticlePostDTO columnArticlePostDTO);

    /**
     * 删除专栏
     *
     * @param columnId
     */
    void deleteColumn(Long columnId);

    /**
     * 删除专栏文章
     *
     * @param id
     */
    void deleteColumnArticle(Long id);

    /**
     * 通过关键词，从标题中找出相似的进行推荐，只返回主键 + 标题
     *
     * @param key
     * @return
     */
    List<SimpleColumnDTO> listSimpleColumnBySearchKey(String key);

    CommonPageVo<ColumnDTO> getColumnList(SearchColumnDTO req);

    CommonPageVo<ColumnArticleDTO> getColumnArticleList(SearchColumnArticleDTO searchColumnArticleDTO);

    void sortColumnArticleApi(SortColumnArticleDTO sortColumnArticleDTO);

    void sortColumnArticleByIDApi(SortColumnArticleByIdDTO sortColumnArticleByIdDTO);
}
