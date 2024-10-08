package com.hpl.count.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/3 19:01
 */
@Data
public class ArticleCountInfoDTO {

    /** 文章点赞数 */
    private Integer  praiseCount;

    /** 文章被阅读数 */
    private Integer  readCount;

    /** 文章被收藏数 */
    private Integer  collectionCount;

    /** 评论数 */
    private Integer commentCount;

    public ArticleCountInfoDTO() {
        praiseCount = 0;
        readCount = 0;
        collectionCount = 0;
        commentCount = 0;
    }
}
