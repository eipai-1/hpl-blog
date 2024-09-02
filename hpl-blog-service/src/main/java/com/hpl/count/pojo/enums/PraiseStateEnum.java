package com.hpl.statistic.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/8/2 18:12
 */
@Getter
public enum PraiseStateEnum {

    NOT_PRAISED(0, "未点赞"),
    PRAISED(1, "已点赞"),
    CANCEL_PRAISED(2, "取消点赞");


    private final Integer code;
    private final String desc;

    PraiseStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
