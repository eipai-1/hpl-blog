package com.hpl.count.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/8/3 8:45
 */
@Getter
public enum CommentStateEnum {

    NOT_COMMENTED(0, "未评论"),
    COMMENTED(1, "已评论"),
    CANCEL_COMMENTED(2, "取消评论");

    private final Integer code;
    private final String desc;

    CommentStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
