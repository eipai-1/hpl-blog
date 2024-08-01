package com.hpl.media.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/31 9:11
 */
@Data
public class VideoPostDTO {

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String remark;
}