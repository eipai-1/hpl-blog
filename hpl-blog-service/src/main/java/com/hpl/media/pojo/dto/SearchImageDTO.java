package com.hpl.media.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/29 12:32
 */
@Data
public class SearchImageDTO implements Serializable {

    @Schema(description = "图片名称")
    private String imageName;

    @Schema(description = "页码")
    private Long pageNum;

    @Schema(description = "页面大小")
    private Long pageSize;


}
