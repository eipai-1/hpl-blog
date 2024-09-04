package com.hpl.article.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/9/4 13:39
 */
@Data
public class ArticleSearchDTO {

    private String keyword;

    private CategoryTreeDTO categoryTreeDTO;
}
