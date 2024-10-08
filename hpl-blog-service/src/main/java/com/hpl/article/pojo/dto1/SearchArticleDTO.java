package com.hpl.article.pojo.dto1;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/6 11:20
 */
@Data
//@ApiModel("文章查询")
public class SearchArticleDTO {

    // 文章标题
//    @ApiModelProperty("文章标题")
    private String title;

//    @ApiModelProperty("文章ID")
    private Long articleId;

//    @ApiModelProperty("作者ID")
    private Long userId;

//    @ApiModelProperty("作者名称")
    private String userName;

//    @ApiModelProperty("文章状态: 0-未发布，1-已发布，2-审核")
    private Integer status;

//    @ApiModelProperty("是否官方: 0-非官方，1-官方")
    private Integer officalState;

//    @ApiModelProperty("是否置顶: 0-不置顶，1-置顶")
    private Integer toppingState;

//    @ApiModelProperty("请求页数，从1开始计数")
    private long pageNumber;

//    @ApiModelProperty("请求页大小，默认为 10")
    private long pageSize;
}

