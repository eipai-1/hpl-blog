package com.hpl.statistic.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.statistic.pojo.enums.DocumentTypeEnum;
import com.hpl.article.service.ArticleReadService;
import com.hpl.statistic.mapper.ReadCountMapper;
import com.hpl.statistic.pojo.constant.CountConstant;
import com.hpl.statistic.pojo.dto.ArticleFootCountDTO;
import com.hpl.statistic.pojo.entity.ReadCount;
import com.hpl.statistic.service.ReadCountService;
import com.hpl.user.service.UserFootService;
import com.hpl.user.service.UserRelationService;
import com.hpl.util.MapUtil;
import com.hpl.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author : rbe
 * @date : 2024/7/3 18:29
 */
@Slf4j
@Service
public class ReadCountServiceImpl extends ServiceImpl<ReadCountMapper, ReadCount> implements ReadCountService {

    @Resource
    private UserFootService userFootService;

    @Resource
    private UserRelationService userRelationService;

//    @Resource
//    private ArticleReadService articleReadService;

    @Resource
    private ReadCountMapper readCountMapper;



    @Override
    public ArticleFootCountDTO queryArticleCountInfoByArticleId(Long articleId) {
        ArticleFootCountDTO res = userFootService.countArticleByArticleId(articleId);
        if (res == null) {
            res = new ArticleFootCountDTO();
        } else {
            //todo 完善评论服务在启用
//            res.setCommentCount(commentReadService.queryCommentCount(articleId));
            res.setCommentCount(123);
        }
        return res;
    }


    @Override
    public ArticleFootCountDTO queryArticleCountInfoByUserId(Long userId) {
        return userFootService.countArticleByUserId(userId);
    }

    /**
     * 查询评论的点赞数
     *
     * @param commentId
     * @return
     */
    @Override
    public Long queryCommentPraiseCount(Long commentId) {
        return userFootService.countCommentPraise(commentId);

    }

//    @Override
//    public StatisticUserInfoDTO queryUserStatisticInfo(Long userId) {
//        Map<String, Integer> ans = RedisClient.hGetAll(CountConstants.USER_STATISTIC_INFO + userId, Integer.class);
//        UserStatisticInfoDTO info = new UserStatisticInfoDTO();
//        info.setFollowCount(ans.getOrDefault(CountConstants.FOLLOW_COUNT, 0));
//        info.setArticleCount(ans.getOrDefault(CountConstants.ARTICLE_COUNT, 0));
//        info.setPraiseCount(ans.getOrDefault(CountConstants.PRAISE_COUNT, 0));
//        info.setCollectionCount(ans.getOrDefault(CountConstants.COLLECTION_COUNT, 0));
//        info.setReadCount(ans.getOrDefault(CountConstants.READ_COUNT, 0));
//        info.setFansCount(ans.getOrDefault(CountConstants.FANS_COUNT, 0));
//        return info;
//    }

    @Override
    public ArticleFootCountDTO getArticleStatisticInfo(Long articleId) {
        Map<String, Integer> ans = RedisUtil.hGetAll(CountConstant.ARTICLE_STATISTIC_INFO + articleId, Integer.class);
        ArticleFootCountDTO info = new ArticleFootCountDTO();
        info.setPraiseCount(ans.getOrDefault(CountConstant.PRAISE_COUNT, 0));
        info.setCollectionCount(ans.getOrDefault(CountConstant.COLLECTION_COUNT, 0));
        info.setCommentCount(ans.getOrDefault(CountConstant.COMMENT_COUNT, 0));
        info.setReadCount(ans.getOrDefault(CountConstant.READ_COUNT, 0));
        return info;
    }

    @Override
    public void incrArticleReadCount(Long authorUserId, Long articleId) {
        //todo

        // db层-MySQL的计数+1
        LambdaQueryWrapper<ReadCount> query = Wrappers.lambdaQuery();
        query.eq(ReadCount::getDocumentId, articleId)
                .eq(ReadCount::getDocumentType, DocumentTypeEnum.ARTICLE.getCode());
        ReadCount record = readCountMapper.selectOne(query);

        if (record == null) {
            record = new ReadCount();
            record.setDocumentId(articleId);
            record.setDocumentType(DocumentTypeEnum.ARTICLE.getCode());
            record.setCnt(1);

            readCountMapper.insert(record);
        } else {
            // fixme: 这里存在并发覆盖问题，推荐使用 update read_count set cnt = cnt + 1 where id = xxx
            record.setCnt(record.getCnt() + 1);
            readCountMapper.updateById(record);
        }

        // cache层-redis计数器 +1
        RedisUtil.pipelineAction()
                .add(CountConstant.ARTICLE_STATISTIC_INFO + articleId, CountConstant.READ_COUNT,
                        (connection, key, value) -> connection.hIncrBy(key, value, 1))
                .add(CountConstant.USER_STATISTIC_INFO + authorUserId, CountConstant.READ_COUNT,
                        (connection, key, value) -> connection.hIncrBy(key, value, 1))
                .execute();
    }

//    /**
//     * 每天4:15分执行定时任务，全量刷新用户的统计信息
//     */
//    @Scheduled(cron = "0 15 4 * * ?")
//    public void autoRefreshAllUserStatisticInfo() {
//        Long now = System.currentTimeMillis();
//        log.info("开始自动刷新用户统计信息");
//        Long userId = 0L;
//        int batchSize = 20;
//        while (true) {
//            List<Long> userIds = userDao.scanUserId(userId, batchSize);
//            userIds.forEach(this::refreshUserStatisticInfo);
//            if (userIds.size() < batchSize) {
//                userId = userIds.get(userIds.size() - 1);
//                break;
//            } else {
//                userId = userIds.get(batchSize - 1);
//            }
//        }
//        log.info("结束自动刷新用户统计信息，共耗时: {}ms, maxUserId: {}", System.currentTimeMillis() - now, userId);
//    }


    /**
     * 获取阅读数最多的文章
     * @return
     */
    @Override
    public List<ReadCount> getTopCountByCategoryId(){
        return lambdaQuery()
                .eq(ReadCount::getDocumentType, DocumentTypeEnum.ARTICLE.getCode())
                .orderByDesc(ReadCount::getCnt)
                .list();
    }

    /**
     * 根据文章ID获取阅读数量
     * 本方法通过查询数据库中与特定文章ID相关的阅读计数记录来获取文章的阅读数量
     * 使用了MP Lambda查询构造器来构建查询条件，提高了查询的可读性和简便性
     *
     * @param articleId 文章ID，用于指定特定的文章
     * @return 返回文章的阅读数量如果不存在，则返回null
     */
    @Override
    public Integer getArticleReadCount(Long articleId){
        return lambdaQuery()
                .eq(ReadCount::getDocumentId, articleId)
                .eq(ReadCount::getDocumentType, DocumentTypeEnum.ARTICLE.getCode())
                .one().getCnt();
    }


    /**
     * 更新用户的统计信息
     *
     * @param userId
     */
    @Override
    public void refreshUserStatisticInfo(Long userId) {
        // 用户的文章点赞数，收藏数，阅读计数
        ArticleFootCountDTO count = userFootService.countArticleByUserId(userId);
        if (count == null) {
            count = new ArticleFootCountDTO();
        }

        // 获取关注数
        Long followCount = userRelationService.queryUserFollowCount(userId);
        // 粉丝数
        Long fansCount = userRelationService.queryUserFansCount(userId);

        // 查询用户发布的文章数
        ArticleReadService articleReadService = SpringUtil.getBean(ArticleReadService.class);
        Long articleNum = articleReadService.getCountByAuthorId(userId);

        String key = CountConstant.USER_STATISTIC_INFO + userId;
        RedisUtil.hMSet(key, MapUtil.create(CountConstant.PRAISE_COUNT, count.getPraiseCount(),
                CountConstant.COLLECTION_COUNT, count.getCollectionCount(),
                CountConstant.READ_COUNT, count.getReadCount(),
                CountConstant.FANS_COUNT, fansCount,
                CountConstant.FOLLOW_COUNT, followCount,
                CountConstant.ARTICLE_COUNT, articleNum));

    }


    @Override
    public void refreshArticleStatisticInfo(Long articleId) {
        ArticleFootCountDTO res = userFootService.countArticleByArticleId(articleId);
        if (res == null) {
            res = new ArticleFootCountDTO();
        } else {
            //todo 别忘了补回来
//            res.setCommentCount(commentReadService.queryCommentCount(articleId));
        }

        RedisUtil.hMSet(CountConstant.ARTICLE_STATISTIC_INFO + articleId,
                MapUtil.create(CountConstant.COLLECTION_COUNT, res.getCollectionCount(),
                        CountConstant.PRAISE_COUNT, res.getPraiseCount(),
                        CountConstant.READ_COUNT, res.getReadCount(),
                        CountConstant.COMMENT_COUNT, res.getCommentCount()
                )
        );
    }



}