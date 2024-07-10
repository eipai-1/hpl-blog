package com.hpl.article.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/3 18:54
 */
@Getter
public enum CommentStateEnum {

    NOT_COMMENT(0, "未评论"),
    COMMENT(1, "已评论"),
    DELETE_COMMENT(2, "删除评论");

    CommentStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static CommentStateEnum formCode(Integer code) {
        for (CommentStateEnum value : CommentStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CommentStateEnum.NOT_COMMENT;
    }
}

