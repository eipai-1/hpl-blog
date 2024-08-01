package com.hpl.media.pojo.enums;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/7/28 18:15
 */
@Getter
public enum MediaBucketEnum {
    IMAGE(1, "hpl-image"),
    VIDEO(2, "hpl-video"),
    MUSIC(3, "hpl-music");

    private final Integer code;
    private final String bucketName;

    MediaBucketEnum(Integer code, String bucketName) {
        this.code = code;
        this.bucketName = bucketName;
    }


}
