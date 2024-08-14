package com.hpl.user.permission;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/6/30 14:43
 */
@Getter
public enum UserRole {

    /** 所有用户 */
    VISITOR(-1,"游客"),

    /** 登录用户 */
    USER(0,"普通用户"),


    /** 管理员 */
    ADMIN(1,"管理员"),


    ;

    private final Integer code;
    private final String desc;

    UserRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
