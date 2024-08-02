package com.hpl.article.pojo.dto1;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/10 14:30
 */
@Data
public class ArticleOtherDTO {
    // 文章的阅读类型
    private Integer readType;
    // 教程的翻页
    private ColumnArticleFlipDTO flip;
}
