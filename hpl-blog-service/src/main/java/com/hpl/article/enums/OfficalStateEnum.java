package com.hpl.article.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/5 9:56
 */
@Getter
public enum OfficalStateEnum {

    NOT_OFFICAL(0, "非官方"),
    OFFICAL(1, "官方");

    OfficalStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static OfficalStateEnum formCode(Integer code) {
        for (OfficalStateEnum value : OfficalStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return OfficalStateEnum.NOT_OFFICAL;
    }
}
