package com.hpl.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto1.SimpleUserInfoDTO;
import com.hpl.count.pojo.dto.ArticleCountInfoDTO;
import com.hpl.count.pojo.dto.StatisticUserFootDTO;
import com.hpl.user.pojo.entity.UserFoot;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
public interface UserFootService extends IService<UserFoot> {

    /**
     * 点赞或取消点赞文章
     * @param articleId
     */
    void praiseArticle(Long articleId);

    /**
     * 收藏或取消收藏文章
     * @param articleId
     */
    void collectArticle(Long articleId);




    /**
     * 查询用户记录，用于判断是否点过赞、是否评论、是否收藏过
     *
     * @param documentId
     * @param type
     * @param userId
     * @return
     */
    UserFoot getByDocIdAndUserId(Long documentId, Integer type, Long userId);

    /**
     * 保存或更新状态信息
     *
     * @param documentType    文档类型：博文 + 评论
     * @param documentId      文档id
     * @param authorId        作者
     * @param userId          操作人
     * @param operateTypeEnum 操作类型：点赞，评论，收藏等
     * @return
     */
//    UserFoot saveOrUpdateUserFoot(DocumentTypeEnum documentType, Long documentId, Long authorId, Long userId, OperateTypeEnum operateTypeEnum);

//    /**
//     * 保存评论足迹
//     * 1. 用户文章记录上，设置为已评论
//     * 2. 若改评论为回复别人的评论，则针对父评论设置为已评论
//     *
//     * @param comment             保存评论入参
//     * @param articleAuthor       文章作者
//     * @param parentCommentAuthor 父评论作者
//     */
//    void saveCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);
//
//    /**
//     * 删除评论足迹
//     *
//     * @param comment             保存评论入参
//     * @param articleAuthor       文章作者
//     * @param parentCommentAuthor 父评论作者
//     */
//    void removeCommentFoot(CommentDO comment, Long articleAuthor, Long parentCommentAuthor);


    /**
     * todo 待优化 我怎么感觉返回id有点多余，因为文章我都遍历出来了
     * 查询已读文章列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
//    List<Long> listReadedAIdsByUId(Long userId, CommonPageParam pageParam);

    /**
     * 查询收藏文章列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
//    List<Long> listCollectionedAIdsByUId(Long userId, CommonPageParam pageParam);

    /**
     * 查询文章的点赞用户信息
     *
     * @param articleId
     * @return
     */
    List<SimpleUserInfoDTO> getArticlePraisedUsers(Long articleId);



    StatisticUserFootDTO getFootCount();


    /**
     * 查询文章计数信息
     *
     * @param articleId
     * @return
     */
    ArticleCountInfoDTO countArticleByArticleId(Long articleId);

    ArticleCountInfoDTO countArticleByUserId(Long userId);

    void handleUpdateUserFoot();

//    Long countCommentPraise(Long commentId);

}
