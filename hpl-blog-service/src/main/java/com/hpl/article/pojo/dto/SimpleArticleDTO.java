package com.hpl.article.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/9/18 21:36
 */

@Data
@AllArgsConstructor
public class SimpleArticleDTO {

    private Long articleId;

    private String title;
}
