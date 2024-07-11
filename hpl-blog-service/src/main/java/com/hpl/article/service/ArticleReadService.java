package com.hpl.article.service;


import com.hpl.article.pojo.entity.ArticleTag;
import com.hpl.article.pojo.dto.ArticleDTO;
import com.hpl.article.pojo.dto.SimpleArticleDTO;
import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.enums.HomeSelectEnum;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.pojo.CommonPageVo;

import java.util.List;
import java.util.Map;

/**
 * @author : rbe
 * @date : todo
 */
public interface ArticleReadService {

    /**
     * 查询基础的文章信息
     *
     * @param articleId
     * @return
     */
    Article getById(Long articleId);

    /**
     * 提取文章摘要
     *
     * @param content
     * @return
     */
    String pickSummary(String content);

    /**
     * 分页查询文章标签列表
     *
     * @param articleId
     * @return
     */
    CommonPageVo<TagDTO> listTagsById(Long articleId);


    /**
     * 查询文章详情，包括正文内容，分类、标签等信息
     *
     * @param articleId
     * @return
     */
    ArticleDTO getArticleInfoById(Long articleId);


    /**
     * 查询文章所有的关联信息，正文，分类，标签，阅读计数+1，当前登录用户是否点赞、评论过
     *
     * @param articleId   文章id
     * @param currentUser 当前查看的用户ID
     * @return
     */
    ArticleDTO getFullArticleInfo(Long articleId, Long currentUser);

    /**
     * 查询某个分类下的文章，支持翻页
     *
     * @param categoryId
     * @param pageParam
     * @return
     */
    CommonPageListVo<ArticleDTO> listArticlesByCategory(Long categoryId, CommonPageParam pageParam);

    /**
     * 文章实体补齐统计、作者、分类标签等信息
     *
     * @param records
     * @param pageSize
     * @return
     */
    CommonPageListVo<ArticleDTO> buildArticleListVo(List<Article> records, long pageSize);

    /**
     * 获取 Top 文章
     *
     * @param categoryId
     * @return
     */
    List<ArticleDTO> getTopArticlesByCategoryId(Long categoryId);


    /**
     * 获取分类文章数
     *
     * @param categoryId
     * @return
     */
    Long getCountByCategoryId(Long categoryId);

    /**
     * 根据分类统计文章计数
     *
     * @return
     */
    Map<Long, Long> queryArticleCountsAndCategory();

    /**
     * 查询某个标签下的文章，支持翻页
     *
     * @param tagId
     * @param page
     * @return
     */
    //todo
    CommonPageListVo<ArticleDTO> listArticlesByTag(Long tagId, CommonPageParam page);

    /**
     * 根据关键词匹配标题，查询用于推荐的文章列表，只返回 articleId + title
     *
     * @param key
     * @return
     */
    //todo 整合es，先不处理
//    List<SimpleArticleDTO> querySimpleArticleBySearchKey(String key);

    /**
     * 根据查询条件查询文章列表，支持翻页
     *
     * @param key
     * @param page
     * @return
     */
    CommonPageListVo<ArticleDTO> listArticlesBySearchKey(String key, CommonPageParam page);

    /**
     * 查询用户的文章列表
     *
     * @param userId
     * @param pageParam
     * @param select
     * @return
     */
    CommonPageListVo<ArticleDTO> listArticlesByUserAndType(Long userId, CommonPageParam pageParam, HomeSelectEnum select);

    /**
     * 查询热门文章
     *
     * @param pageParam
     * @return
     */
    CommonPageListVo<SimpleArticleDTO> listHotArticlesForRecommend(CommonPageParam pageParam);

    /**
     * 查询作者的文章数
     * 传入为空，查询所以文章数
     *
     * @param authorId
     * @return
     */
    Long getCountByAuthorId(Long authorId);

    /**
     * 文章关联推荐
     *
     * @param article
     * @param pageParam
     * @return
     */
    CommonPageListVo<ArticleDTO> relatedRecommend(Long article, CommonPageParam pageParam);


    List<SimpleArticleDTO> listArticlesOrderById(long lastId, int scanSize);

    List<ArticleTag> listTagsByArticleId(Long articleId);

    List<Article> listRelatedArticlesOrderByReadCount(Long categoryId, List<Long> tagIds, CommonPageParam page);
}
