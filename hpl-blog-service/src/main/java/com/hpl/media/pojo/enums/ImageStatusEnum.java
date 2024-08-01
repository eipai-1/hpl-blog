package com.hpl.media.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/29 9:59
 */
@Getter
public enum ImageStatusEnum {

    //正常展示，不正常展示
    SHOW(1, "正常展示"),
    NOT_SHOW(0, "取消展示");

    private final Integer code;
    private final String desc;

    ImageStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
