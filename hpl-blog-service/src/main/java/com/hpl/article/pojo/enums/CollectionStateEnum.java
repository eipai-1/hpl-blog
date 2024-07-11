package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/3 18:53
 */
@Getter
public enum CollectionStateEnum {

    NOT_COLLECTION(0, "未收藏"),
    COLLECTION(1, "已收藏"),
    CANCEL_COLLECTION(2, "取消收藏");

    CollectionStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static CollectionStateEnum formCode(Integer code) {
        for (CollectionStateEnum value : CollectionStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CollectionStateEnum.NOT_COLLECTION;
    }
}