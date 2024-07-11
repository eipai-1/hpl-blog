package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 保存Category请求参数
 *
 * @author : rbe
 * @date : 2024/7/6 17:13
 */
@Data
public class CategoryPostDTO implements Serializable {

    /**
     * ID
     */
    private Long categoryId;

    /**
     * 类目名称
     */
    private String category;

    /**
     * 排序
     */
    private Integer rank;
}
