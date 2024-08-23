package com.hpl.article.pojo.dto1;

import com.hpl.article.pojo.dto.TagDTO;
import com.hpl.statistic.pojo.dto.ArticleCountInfoDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 16:25
 */
@Data
public class ArticleDTO implements Serializable {
    private static final long serialVersionUID = -793906904770296838L;

    private Long articleId;

    /**
     * 文章类型：1-博文，2-问答
     */
    private Integer articleType;

    /**
     * 作者uid
     */
    private Long authorId;

    /**
     * 作者名
     */
    private String authorName;

    /**
     * 作者头像
     */
    private String authorAvatar;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 短标题
     */
    private String shortTitle;

    /**
     * 简介
     */
    private String summary;

    /**
     * 封面
     */
    private String cover;

    /**
     * 正文
     */
    private String content;

    /**
     * 文章来源
     */
    private String sourceType;

    /**
     * 原文地址
     */
    private String sourceUrl;

    /**
     * 0 未发布 1 已发布
     */
    private Integer status;

    /**
     * 是否官方
     */
    private Integer officalState;

    /**
     * 是否置顶
     */
    private Integer toppingState;

    /**
     * 是否加精
     */
    private Integer creamState;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 最后更新时间
     */
    private LocalDateTime lastUpdateTime;

    /**
     * 分类
     */
    private CategoryDTO category;

    /**
     * 标签
     */
    private List<TagDTO> tags;

    /**
     * 表示当前查看的用户是否已经点赞过
     */
    private Boolean praised;

    /**
     * 表示当用户是否评论过
     */
    private Boolean commented;

    /**
     * 表示当前用户是否收藏过
     */
    private Boolean collected;

    /** 文章点赞数 */
    private Integer  praiseCount;

    /** 文章被阅读数 */
    private Integer  readCount;

    /** 文章被收藏数 */
    private Integer  collectionCount;

    /** 评论数 */
    private Integer commentCount;

    /**
     * 点赞用户信息
     */
    private List<SimpleUserInfoDTO> praisedUsers;
}
