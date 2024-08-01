package com.hpl.media.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/30 18:27
 */
@Data
public class AudioPostDTO {

    private String fileName;

    private String contentType;

    private Long fileSize;

    private String remark;
}