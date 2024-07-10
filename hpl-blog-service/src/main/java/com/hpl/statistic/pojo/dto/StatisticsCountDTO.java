package com.hpl.statistic.pojo.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/9 15:06
 */
@Data
@Builder
public class StatisticsCountDTO {

    /** PV 数量 */
    private Long pvCount;

    /** 总用户数 */
    private Long userCount;

    /** 总评论数 */
    private Long commentCount;

    /** 总阅读数 */
    private Long readCount;

    /** 总点赞数 */
    private Long likeCount;

    /** 总收藏数 */
    private Long collectCount;

    /** 文章数量 */
    private Long articleCount;

    /** 教程数量 */
    private Long tutorialCount;

//    /** 星球付费人数 */
//    private Integer starPayCount;
}
