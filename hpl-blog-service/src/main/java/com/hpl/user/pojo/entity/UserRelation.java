package com.hpl.user.pojo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/6/29 18:48
 */
@Data
@TableName("user_relation")
public class UserRelation implements Serializable {

    @TableId(type= IdType.AUTO)
    private Long id;

    /** 主用户ID */
    private Long userId;

    /** 粉丝用户ID */
    private Long followUserId;

    /** 关注状态: 0-未关注，1-已关注，2-取消关注 */
    private Integer followState;
}