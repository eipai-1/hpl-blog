package com.hpl.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 专栏文章
 *
 * @author YiHui
 * @date 2022/9/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("column_article")
public class ColumnArticle extends CommonEntity {
    private static final long serialVersionUID = -2372103913090667453L;

    /** 专栏id */
    private Long columnId;

    /** 文章id */
    private Long articleId;

    /** 章节顺序，越小越靠前 */
    private Integer section;

    /** 专栏类型：免费、登录阅读、收费阅读等 */
    private Integer readType;
}
