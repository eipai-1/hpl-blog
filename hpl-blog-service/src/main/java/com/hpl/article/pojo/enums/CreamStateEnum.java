package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/6 12:51
 */
@Getter
public enum CreamStateEnum {

    NOT_CREAM(0, "不加精"),
    CREAM(1, "加精");

    CreamStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static CreamStateEnum formCode(Integer code) {
        for (CreamStateEnum value : CreamStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return CreamStateEnum.NOT_CREAM;
    }
}
