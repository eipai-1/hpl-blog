package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : rbe
 * @date : 2024/7/6 11:18
 */
@Data
public class ArticleAdminDTO implements Serializable {
    private static final long serialVersionUID = -793906904770296838L;

    /** 文章id */
    private Long articleId;

    /** 作者uid */
    private Long authorId;

    /** 作者名 */
    private String authorName;

    /** 作者头像 */
    private String authorAvatar;

    /** 文章标题 */
    private String title;

    /** 短标题 */
    private String shortTitle;

    /** 封面 */
    private String cover;

    /** 0 未发布 1 已发布 */
    private Integer status;

    /** 是否官方 */
    private Integer officalState;

    /** 是否置顶 */
    private Integer toppingState;

    /** 是否加精 */
    private Integer creamState;

    /** 更新时间 */
    private Date updateTime;

}