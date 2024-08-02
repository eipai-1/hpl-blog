package com.hpl.article.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/6 18:41
 */
@Data
public class SearchTagDTO {
    // 标签名称
    private String tag;
    // 分页
    private Long pageNumber;
    private Long pageSize;
}
