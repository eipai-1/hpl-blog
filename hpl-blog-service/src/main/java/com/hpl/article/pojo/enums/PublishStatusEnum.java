package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/3 16:45
 */
@Getter
public enum PublishStatusEnum {

    UN_PUBLISHED(0, "未发布"),
    PUBLISHED(1,"已发布");

    PublishStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    private final int code;
    private final String desc;

}
