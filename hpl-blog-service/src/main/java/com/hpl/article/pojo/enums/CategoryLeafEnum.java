package com.hpl.article.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/8/21 17:01
 */
@Getter
public enum CategoryLeafEnum {

    IS_LEAF(1, "叶子结点"),
    NOT_LEAF(0, "非叶子结点");

    private final int code;
    private final String desc;

    CategoryLeafEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
