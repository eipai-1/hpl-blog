package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/8/7 9:21
 */
@Data
public class SearchMyArticleDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String searchKey;

}
