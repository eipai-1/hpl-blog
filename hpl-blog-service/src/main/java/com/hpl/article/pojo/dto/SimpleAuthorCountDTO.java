package com.hpl.article.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/8/1 15:26
 */
@Data
public class SimpleAuthorCountDTO {

    /**
     * 作者id
     */
    Long authorId;

    /**
     * 文章数量
     */
    Long articleCount;
}
