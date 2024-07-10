package com.hpl.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章详情
 *
 * DO 对应数据库实体类
 *
 * @author louzai
 * @date 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article_detail")
public class ArticleDetail extends CommonEntity {

    private static final long serialVersionUID = 1L;

    /** 文章ID */
    private Long articleId;

    /** 版本号 */
    private Long version;

    /** 文章内容 */
    private String content;

    /** 删除状态 0-未删除 1-已删除 */
    private Integer deleted;
}
