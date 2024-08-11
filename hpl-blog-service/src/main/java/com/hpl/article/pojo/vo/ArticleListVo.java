package com.hpl.article.pojo.vo;

import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.article.pojo.entity.Tag;
import com.hpl.statistic.pojo.dto.ArticleCountInfoDTO;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 14:21
 */
@Data
public class ArticleListVo {
    private static final long serialVersionUID = -793906904770296838L;

    /**
     * 文章ID
     */
    private Long articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 文章摘要
     */
    private String summary;

    /**
     * 文章的更新时间，记录文章最后一次修改的时间
     */
    private LocalDateTime updateTime;

    /**
     * 文章所属的分类名称
     */
    private String categoryName;

    /**
     * 文章的标签列表
     */
    private List<TagDTO> tags;

    /**
     * 文章的底部计数信息，包括阅读数、点赞数等
     */
    private ArticleCountInfoDTO countInfo;

    /**
     * 文章作者的ID
     */
    private Long authorId;

    /**
     * 文章作者的姓名
     */
    private String authorName;

    /**
     * 文章作者的头像链接
     */
    private String authorAvatar;

}