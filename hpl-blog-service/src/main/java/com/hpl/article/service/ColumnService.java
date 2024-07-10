package com.hpl.article.service;


import com.hpl.article.dto.ColumnDTO;
import com.hpl.article.dto.SimpleArticleDTO;
import com.hpl.article.entity.ColumnArticle;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;

import java.util.List;

/**
 * @author YiHui
 * @date 2022/9/14
 */
public interface ColumnService {
    /**
     * 根据文章id，构建对应的专栏详情地址
     *
     * @param articleId 文章主键
     * @return 专栏详情页
     */
    ColumnArticle getColumnArticleRelation(Long articleId);

    /**
     * 专栏列表
     *
     * @param pageParam
     * @return
     */
    CommonPageListVo<ColumnDTO> listColumn(CommonPageParam pageParam);

    /**
     * 只查询基本的专栏信息，不需要统计、作者等信息
     *
     * @param columnId
     * @return
     */
    ColumnDTO queryBasicColumnInfo(Long columnId);


    /**
     * 专栏详情
     *
     * @param columnId
     * @return
     */
    ColumnDTO queryColumnInfo(Long columnId);


    /**
     * 获取专栏中的第N篇文章
     *
     * @param columnId
     * @param order
     * @return
     */
    ColumnArticle queryColumnArticle(long columnId, Integer order);



    /**
     * 专栏 + 文章列表详情
     *
     * @param columnId
     * @return
     */
    List<SimpleArticleDTO> queryColumnArticles(long columnId);

    /**
     * 返回教程数量
     *
     * @return
     */
    Long getTutorialCount();


}
