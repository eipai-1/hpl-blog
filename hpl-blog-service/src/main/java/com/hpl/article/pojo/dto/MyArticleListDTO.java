package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/7 9:21
 */
@Data
public class MyArticleListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long articleId;
    private Long authorId;
    private String title;
    private String shortTitle;
    private String picture;
    private String summary;

    private String category;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;

}
