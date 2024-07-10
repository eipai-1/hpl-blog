package com.hpl.article.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/7 15:39
 */
@Data
//@ApiModel("教程排序")
public class SortColumnArticleDTO implements Serializable {
    // 排序前的文章 ID
//    @ApiModelProperty("排序前的文章 ID")
    private Long activeId;

    // 排序后的文章 ID
//    @ApiModelProperty("排序后的文章 ID")
    private Long overId;

}