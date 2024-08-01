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


}