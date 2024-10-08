package com.hpl.article.pojo.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.Set;

/**
 * @author : rbe
 * @date : 2024/7/6 9:29
 */
@Data
public class ArticlePostDTO implements Serializable {

    /** 文章ID， 当存在时，表示更新文章 */
    @Schema(description = "文章ID， 当存在时，表示更新文章" )
    private Long articleId;

    /** 文章标题 */
    @Schema(description = "文章标题" )
    private String title;

    /** 专栏ID */
    @Schema(description = "关联的专栏ID" )
    private Long columnId;

    /** 分类 */
    @Schema(description = "分类" )
    private String categoryId;

    /** 标签 */
    @Schema(description = "标签" )
    private Set<Long> tagIds;

    /** 来源：1-转载，2-原创，3-翻译 */
    @Schema(description = "来源：1-转载，2-原创，3-翻译" )
    private Integer sourceType;

    /** 原文地址 */
    @Schema(description = "原文地址" )
    private String sourceUrl;

    /** 正文内容 */
    @Schema(description = "正文内容" )
    private String content;

    /** 状态：0-暂存，1-已发布 2-已删除*/
    @Schema(description = "状态：0-暂存，1-已发布 2-已删除" )
    private Integer status;




}
