package com.hpl.article.pojo.dto1;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/7 11:40
 */
@Data
//@ApiModel("教程配套文章查询")
public class SearchColumnArticleDTO {

    // 教程名称
//    @ApiModelProperty("教程名称")
    private String column;

    // 教程 ID
//    @ApiModelProperty("教程 ID")
    private Long columnId;

    // 文章标题
//    @ApiModelProperty("文章标题")
    private String articleTitle;

//    @ApiModelProperty("请求页数，从1开始计数")
    private long pageNumber;

//    @ApiModelProperty("请求页大小，默认为 10")
    private long pageSize;
}
