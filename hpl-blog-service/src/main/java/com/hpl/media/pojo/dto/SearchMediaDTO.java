package com.hpl.media.pojo.dto;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.ToString;

/**
 * @author : rbe
 * @date : 2024/7/13 10:33
 */
@Data
@ToString
public class SearchMediaDTO {

//    @ApiModelProperty("媒资文件名称")
    private String filename;

//    @ApiModelProperty("媒资类型")
    private String fileType;

//    @ApiModelProperty("审核状态")
    @Schema(description = "审核状态")
    private String auditStatus;

}
