package com.hpl.article.pojo.dto;

import com.hpl.comment.pojo.dto.TopCommentDTO;
import lombok.Data;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 18:19
 */
@Data
public class ColumnArticlesDTO {

    /** 专栏详情 */
    private Long column;

    /** 当前查看的文章 */
    private Integer section;

    /** 文章详情 */
    private ArticleDTO article;

    /**
     * 0 免费阅读
     * 1 要求登录阅读
     * 2 限时免费，若当前时间超过限时免费期，则调整为登录阅读 */
    private Integer readType;

    /** 文章评论 */
    private List<TopCommentDTO> comments;

    /** 热门评论 */
    private TopCommentDTO hotComment;

    /** 文章目录列表 */
    private List<SimpleArticleDTO> articleList;

    // 翻页
    private ArticleOtherDTO other;
}
