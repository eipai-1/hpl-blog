package com.hpl.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/6/29 18:48
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user_relation")
public class UserRelation extends CommomEntity {

    private static final long serialVersionUID = 1L;

    /** 主用户ID */
    private Long userId;

    /** 粉丝用户ID */
    private Long followUserId;

    /** 关注状态: 0-未关注，1-已关注，2-取消关注 */
    private Integer followState;
}