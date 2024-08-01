package com.hpl.article.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/27 18:53
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVo implements Serializable {

    private Long categoryId;

    private String categoryName;

    private Integer rank;

    private Integer status;
}
