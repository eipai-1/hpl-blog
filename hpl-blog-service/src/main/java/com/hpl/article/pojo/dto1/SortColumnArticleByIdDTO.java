package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/7 15:41
 */
@Data
//@ApiModel("教程排序，根据 ID 和新填的排序")
public class SortColumnArticleByIdDTO implements Serializable {
    // 要排序的 id
//    @ApiModelProperty("要排序的 id")
    private Long id;
    // 新的排序
//    @ApiModelProperty("新的排序")
    private Integer sort;
}