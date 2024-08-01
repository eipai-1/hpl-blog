package com.hpl.media.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/13 10:10
 */
@Getter
public enum ProcessStatusEnum {

    UN_START(1, "未处理"),
    SUCCESS(2, "处理成功"),
    FAILED(3, "处理失败");

    private final Integer code ;
    private final String desc ;

    ProcessStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }


}
