package com.hpl.media.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/31 9:09
 */
@Data
public class SearchVideoDTO implements Serializable {

    @Schema(description = "视频名称")
    private String videoName;


}