package com.hpl.statistic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.statistic.pojo.dto.ArticleFootCountDTO;
import com.hpl.statistic.pojo.entity.ReadCount;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 18:29
 */
public interface ReadCountService extends IService<ReadCount> {

    /**
     * 文章计数+1
     *
     * @param authorUserId 作者
     * @param articleId    文章
     * @return 计数器
     */
    void incrArticleReadCount(Long authorUserId, Long articleId);

    List<ReadCount> getTopCountByCategoryId();

    /**
     * 根据文章ID获取阅读数量
     * 本方法通过查询数据库中与特定文章ID相关的阅读计数记录来获取文章的阅读数量
     * 使用了MP Lambda查询构造器来构建查询条件，提高了查询的可读性和简便性
     *
     * @param articleId 文章ID，用于指定特定的文章
     * @return 返回文章的阅读数量如果不存在，则返回null
     */
    Integer getArticleReadCount(Long articleId);

    /**
     * 根据文章ID查询文章计数
     * 本方法直接基于db进行查询相关信息，改用下面的 getArticleStatisticInfo() 方法进行替换
     *
     * @param articleId
     * @return
     */
    @Deprecated
    ArticleFootCountDTO queryArticleCountInfoByArticleId(Long articleId);


    /**
     * 查询用户总阅读相关计数（当前未返回评论数）
     * 本方法直接基于db进行查询相关信息，改用下面的 queryUserStatisticInfo() 方法进行替换
     *
     * @param userId
     * @return
     */
    @Deprecated
    ArticleFootCountDTO queryArticleCountInfoByUserId(Long userId);

    /**
     * 获取评论点赞数量
     *
     * @param commentId
     * @return
     */
    Long queryCommentPraiseCount(Long commentId);


//    /**
//     * 查询用户的相关统计信息
//     *
//     * @param userId
//     * @return 返回用户的 收藏、点赞、文章、粉丝、关注，总的文章阅读数
//     */
//    StatisticUserInfoDTO queryUserStatisticInfo(Long userId);

    /**
     * 查询文章相关的统计信息
     *
     * @param articleId
     * @return 返回文章的 收藏、点赞、评论、阅读数
     */
    ArticleFootCountDTO getArticleStatisticInfo(Long articleId);

    

    /**
     * 刷新用户的统计信息
     *
     * @param userId
     */
    void refreshUserStatisticInfo(Long userId);

    /**
     * 刷新文章的统计信息
     *
     * @param articleId
     */
    void refreshArticleStatisticInfo(Long articleId);


}