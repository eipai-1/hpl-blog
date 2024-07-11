package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/3 18:52
 */
@Getter
public enum ReadStateEnum {

    NOT_READ(0, "未读"),
    READ(1, "已读");

    ReadStateEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final Integer code;
    private final String desc;

    public static ReadStateEnum formCode(Integer code) {
        for (ReadStateEnum value : ReadStateEnum.values()) {
            if (value.getCode().equals(code)) {
                return value;
            }
        }
        return ReadStateEnum.NOT_READ;
    }
}
