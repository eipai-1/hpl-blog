package com.hpl.statistic.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.redis.RedisClient;
import com.hpl.statistic.mapper.TraceCountMapper;
import com.hpl.statistic.pojo.dto.CountAllDTO;
import com.hpl.statistic.pojo.entity.TraceCount;
import com.hpl.statistic.pojo.enums.CollectionStateEnum;
import com.hpl.statistic.pojo.enums.CommentStateEnum;
import com.hpl.statistic.pojo.enums.PraiseStateEnum;
import com.hpl.statistic.service.TraceCountService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * @author : rbe
 * @date : 2024/7/3 18:29
 */
@Slf4j
@Service
public class TraceCountServiceImpl extends ServiceImpl<TraceCountMapper, TraceCount> implements TraceCountService {

    @Resource
    private TraceCountMapper traceCountMapper;

    @Resource
    private RedisClient redisClient;

    /**
     * 查询文章的点赞、评论、收藏数量
     * @param articleId
     */
    @Override
    public CountAllDTO getAllCountById(Long userId, Long articleId){

        CountAllDTO countAllDTO = new CountAllDTO();

        countAllDTO.setPraiseCount(this.getPraisedCount(userId,articleId));
        countAllDTO.setCommentCount(this.getCommentedCount(userId,articleId));
        countAllDTO.setCollectionCount(this.getCollectedCount(userId,articleId));

        return countAllDTO;


    }

    /**
     * 获取文章的点赞数
     * 该方法通过查询数据库中符合条件的点赞记录数量来获取文章的点赞数
     *
     * @param userId    用户ID，用于筛选特定用户点赞的文章如果为null，则不考虑用户筛选条件
     * @param articleId 文章ID，用于筛选特定文章的点赞数如果为null，则不考虑文章筛选条件
     * @return 文章的点赞数如果找不到符合条件的记录，返回0
     */
    @Override
    public Integer getCollectedCount(Long userId, Long articleId){
        // KEY 的形式为CollectedCount:userid:articleid /
        // CollectedCount:userid /  用户的所有收藏数
        // CollectedCount:articleid 用户的点赞收藏数
//        String key = "CollectedCount:";
//        if(userId != null){
//            key += "user:" + userId;
//        }
//        if(articleId != null){
//            key += "article:" + articleId;
//        }
//        Integer count = redisClient.incr(key).intValue();
//        if(count != null){
//            return count;
//        }

        // 创建查询条件封装对象
        LambdaQueryWrapper<TraceCount> wrapper = new LambdaQueryWrapper<>();

        // 设置查询条件为点赞状态
        wrapper.eq(TraceCount::getCollectionState, CollectionStateEnum.COLLECTED.getCode());

        // 如果用户ID不为空，则添加用户ID作为查询条件
        if(userId != null){
            wrapper.eq(TraceCount::getUserId,userId);
        }

        // 如果文章ID不为空，则添加文章ID作为查询条件
        if (articleId != null){
            wrapper.eq(TraceCount::getArticleId,articleId);
        }

        return traceCountMapper.selectCount(wrapper).intValue();

        // 调用Mapper的selectCount方法，根据查询条件统计点赞数
//        Long initCount = traceCountMapper.selectCount(wrapper);
//        log.info("从数据库中获取点赞数：{}",initCount);
//        redisClient.incrByStep(key,initCount);
//        return count;
    }


    /**
     * 获取文章的点赞数
     * 本方法通过查询数据库中符合条件的点赞记录数量来获取文章的点赞数
     *
     * @param userId    用户ID，用于区分不同的用户点赞情况如果为null，则不考虑用户因素
     * @param articleId 文章ID，用于区分不同的文章点赞情况如果为null，则不考虑文章因素
     * @return 返回符合条件的点赞记录数量，如果查询不到任何记录，则返回0
     */
    @Override
    public Integer getPraisedCount(Long userId, Long articleId){

        // 创建查询条件封装对象
        LambdaQueryWrapper<TraceCount> wrapper = new LambdaQueryWrapper<>();

        // 设置查询条件为点赞状态
        wrapper.eq(TraceCount::getPraiseState, PraiseStateEnum.PRAISED.getCode());

        // 如果用户ID不为空，则添加用户ID作为查询条件
        if(userId != null){
            wrapper.eq(TraceCount::getUserId,userId);
        }

        // 如果文章ID不为空，则添加文章ID作为查询条件
        if (articleId != null){
            wrapper.eq(TraceCount::getArticleId,articleId);
        }

        // 执行查询并返回点赞数
        return traceCountMapper.selectCount(wrapper).intValue();
    }

    /**
     * 根据用户ID和文章ID获取点赞数
     * 本方法通过查询数据库中符合条件的点赞记录数来获取点赞数
     *
     * @param userId    用户ID，用于区分不同的用户，如果为null，则不作为查询条件
     * @param articleId 文章ID，用于区分不同的文章，如果为null，则不作为查询条件
     * @return 返回符合条件的点赞数
     */
    @Override
    public Integer getCommentedCount(Long userId, Long articleId) {

        // 创建查询条件封装对象
        LambdaQueryWrapper<TraceCount> wrapper = new LambdaQueryWrapper<>();

        // 设置查询条件为点赞状态
        wrapper.eq(TraceCount::getCommentState, CommentStateEnum.COMMENTED.getCode());

        // 如果用户ID不为空，则添加用户ID作为查询条件
        if (userId != null) {
            wrapper.eq(TraceCount::getUserId, userId);
        }

        // 如果文章ID不为空，则添加文章ID作为查询条件
        if (articleId != null) {
            wrapper.eq(TraceCount::getArticleId, articleId);
        }

        // 调用Mapper的selectCount方法，根据查询条件统计点赞数
        return traceCountMapper.selectCount(wrapper).intValue();
    }
}