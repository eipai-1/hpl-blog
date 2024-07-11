package com.hpl.article.pojo.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/7 10:00
 */
@Data
@Accessors(chain = true)
public class SimpleColumnDTO implements Serializable {

    private static final long serialVersionUID = 3646376715620165839L;

//    @ApiModelProperty("专栏id")
    private Long columnId;

//    @ApiModelProperty("专栏名")
    private String column;

    // 封面
//    @ApiModelProperty("封面")
    private String cover;
}