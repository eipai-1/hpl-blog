package com.hpl.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章标签映射表
 *
 * @author louzai
 * @date 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_tag")
public class ArticleTag extends CommonEntity {

    private static final long serialVersionUID = 1L;

    /** 文章ID */
    private Long articleId;

    /** 标签id */
    private Long tagId;

    /** 删除状态 0-未删除 1-已删除 */
    private Integer deleted;
}
