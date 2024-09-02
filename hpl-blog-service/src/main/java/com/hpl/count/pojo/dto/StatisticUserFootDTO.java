package com.hpl.statistic.pojo.dto;

import lombok.Data;
import lombok.ToString;

/**
 * @author : rbe
 * @date : 2024/7/9 15:08
 */
@Data
@ToString(callSuper = true)
public class StatisticUserFootDTO {

    /** 文章点赞数 */
    private Long praiseCount;

    /** 文章被阅读数 */
    private Long readCount;

    /** 文章被收藏数 */
    private Long collectionCount;

    /** 文章被评论数 */
    private Long commentCount;

    public StatisticUserFootDTO() {
        praiseCount = 0L;
        readCount = 0L;
        collectionCount = 0L;
        commentCount = 0L;
    }
}
