package com.hpl.statistic.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.statistic.pojo.dto.CountAllDTO;
import com.hpl.statistic.pojo.entity.TraceCount;

/**
 * @author : rbe
 * @date : 2024/7/3 18:29
 */
public interface TraceCountService extends IService<TraceCount> {

    /**
     * 查询文章的点赞、评论、收藏数量
     * @param articleId
     */
    CountAllDTO getAllCountByArticleId(Long userId, Long articleId);

    /**
     * 获取文章的点赞数
     * 该方法通过查询数据库中符合条件的点赞记录数量来获取文章的点赞数
     *
     * @param userId    用户ID，用于筛选特定用户点赞的文章如果为null，则不考虑用户筛选条件
     * @param articleId 文章ID，用于筛选特定文章的点赞数如果为null，则不考虑文章筛选条件
     * @return 文章的点赞数如果找不到符合条件的记录，返回0
     */
    Integer getCollectedCount(Long userId,Long articleId);


    /**
     * 获取文章的点赞数
     * 本方法通过查询数据库中符合条件的点赞记录数量来获取文章的点赞数
     *
     * @param userId    用户ID，用于区分不同的用户点赞情况如果为null，则不考虑用户因素
     * @param articleId 文章ID，用于区分不同的文章点赞情况如果为null，则不考虑文章因素
     * @return 返回符合条件的点赞记录数量，如果查询不到任何记录，则返回0
     */
    Integer getPraisedCount(Long userId, Long articleId);


    /**
     * 根据用户ID和文章ID获取点赞数
     * 本方法通过查询数据库中符合条件的点赞记录数来获取点赞数
     *
     * @param userId    用户ID，用于区分不同的用户，如果为null，则不作为查询条件
     * @param articleId 文章ID，用于区分不同的文章，如果为null，则不作为查询条件
     * @return 返回符合条件的点赞数
     */
    Integer getCommentedCount(Long userId, Long articleId);
}