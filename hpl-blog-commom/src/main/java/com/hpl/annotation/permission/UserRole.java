package com.hpl.annotation.permission;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/6/30 14:43
 */
@Getter
public enum UserRole {

    /** 登录用户 */
    LOGIN(0,"普通用户"),


    /** 管理员 */
    ADMIN(1,"管理员"),

    /** 所有用户 */
    ALL(2,"所有用户"),
    ;

    private final Integer code;
    private final String desc;

    UserRole(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
