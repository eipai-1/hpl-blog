package com.hpl.article.pojo.dto1;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/6 17:20
 */
@Data
public class SearchCategoryDTO {
    // 类目名称
    private String category;
    // 分页
    private Long pageNumber;

    private Long pageSize;

}