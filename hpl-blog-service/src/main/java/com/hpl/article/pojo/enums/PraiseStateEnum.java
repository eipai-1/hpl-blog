package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/3 18:52
 */
@Getter
public enum PraiseStateEnum {

    NOT_PRAISE(0, "未点赞"),
    PRAISE(1, "已点赞"),
    CANCEL_PRAISE(2, "取消点赞");

    PraiseStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static PraiseStateEnum formCode(Integer code) {
        for (PraiseStateEnum value : PraiseStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return PraiseStateEnum.NOT_PRAISE;
    }
}
