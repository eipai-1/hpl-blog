package com.hpl.user.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommomEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/6/29 18:32
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("user")
public class User extends CommomEntity {

    private static final long serialVersionUID = 1L;

    /** 第三方用户ID */
    private String thirdAccountId;

    /** 登录方式: 0-微信登录，1-账号密码登录 */
    private Integer loginType;

    /** 删除标记 */
    private Integer deleted;

    /** 登录用户名 */
    private String userName;

    /** 登录密码，密文存储 */
    private String password;

    /** 密码加盐 */
    private String salt;

}
