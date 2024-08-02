package com.hpl.article.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/10 14:30
 */
@Data
public class ColumnArticleFlipDTO {
    String prevHref;
    Boolean prevShow;
    String nextHref;
    Boolean nextShow;
}
