package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/6 12:50
 */
@Getter
public enum ToppingStateEnum {

    NOT_TOPPING(0, "不置顶"),
    TOPPING(1, "置顶");

    ToppingStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static ToppingStateEnum formCode(Integer code) {
        for (ToppingStateEnum value : ToppingStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ToppingStateEnum.NOT_TOPPING;
    }
}
