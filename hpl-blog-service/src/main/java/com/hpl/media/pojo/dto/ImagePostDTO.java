package com.hpl.media.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/28 18:38
 */
@Data
public class ImagePostDTO {

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String remark;
}
