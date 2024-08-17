package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/15 13:53
 */
@Data
public class SimpleDetailDTO implements Serializable {

    private Long articleId;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 0 未发布 1 已发布
     */
    private Integer status;

    /**
     * 分类
     */
    private Long categoryId;



    /**
     * 正文
     */
    private String content;


    /**
     * 标签
     */
    private List<Long> tagIds;


}
