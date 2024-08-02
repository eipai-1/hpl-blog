package com.hpl.article.pojo.dto1;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/7 9:21
 */
@Data
public class ColumnArticlePostDTO implements Serializable {

    /** 主键ID */
    private Long id;

    /** 专栏ID */
    private Long columnId;

    /** 文章ID */
    private Long articleId;

    /** 文章排序 */
    private Integer sort;

    /** 教程标题 */
    private String shortTitle;

    /** 阅读方式 */
    private Integer read;
}