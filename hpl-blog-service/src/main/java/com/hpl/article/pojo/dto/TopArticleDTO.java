package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/2 7:58
 */
@Data
public class TopArticleDTO implements Serializable {

    private Long articleId;

    private String title;

    private Integer cnt;
}