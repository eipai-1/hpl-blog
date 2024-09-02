package com.hpl.statistic.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/8/2 18:11
 */
@Getter
public enum CollectionStateEnum {

    NOT_CONTAINED(0, "未收藏"),
    COLLECTED(1, "已收藏"),
    CANCEL_COLLECTED(2, "取消收藏");



    private final Integer code;
    private final String desc;

    CollectionStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
