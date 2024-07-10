package com.hpl.article.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 文章表
 *
 * @author louzai
 * @date 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("article")
public class Article extends CommonEntity {
    private static final long serialVersionUID = 1L;

    /** 作者id */
    private Long authorId;

    /** 文章类型：1-博文，2-问答, 3-专栏文章 */
    private Integer articleType;

    /** 文章标题 */
    private String title;

    /** 短标题 */
    private String shortTitle;

    /** 文章头图 */
    private String picture;

    /** 文章摘要 */
    private String summary;

    /** 类目ID */
    private Long categoryId;

    /** 来源：1-转载，2-原创，3-翻译 */
    private Integer source;

    /** 原文地址 */
    private String sourceUrl;

    /** 状态：0-未发布，1-已发布 */
    private Integer status;

    /** 是否官方 */
    private Integer officalState;

    /** 是否置顶 */
    private Integer toppingState;

    /** 是否加精 */
    private Integer creamState;

    /** 删除状态：0-未删除，1-已删除 */
    private Integer deleted;
}
