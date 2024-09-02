package com.hpl.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/6/29 18:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_foot")
public class UserFoot extends CommonEntity {

    /** 用户ID */
    private Long userId;

    /** 文档ID（文章/评论） */
    private Long documentId;

    /** 文档类型：1-文章，2-评论 */
    private Integer documentType;

    /** 发布该文档的用户ID  */
    private Long documentUserId;

    /** 收藏状态: 0-未收藏，1-已收藏 */
    private Integer collected;

    /** 评论状态: 0-未评论，1-已评论 */
    private Integer commented;

    /** 点赞状态: 0-未点赞，1-已点赞 */
    private Integer praised;
}
