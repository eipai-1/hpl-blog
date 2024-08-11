package com.hpl.media.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/30 17:53
 */
@Data
public class SearchAudioDTO implements Serializable {

    @Schema(description = "音频图片名称")
    private String audioName;

    @Schema(description = "页码")
    private Long pageNum;

    @Schema(description = "页面大小")
    private Long pageSize;

}