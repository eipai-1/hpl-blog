package com.hpl.column.pojo.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/7 11:04
 */
@Data
public class MyColumnListDTO implements Serializable {
    /*********************
     * 专栏信息
     *********************/

    /** 专栏id */
    private Long columnId;

    /** 专栏名 */
    private String columnName;

    /** 说明 */
    private String introduction;

    /** 封面 */
    private String cover;

    /** 发布时间 */
    private LocalDateTime createTime;

    /** 排序 */
    private Integer section;

    /*********************
     * 统计信息
     *********************/

    /** 专栏已更新的文章数 */
    private Integer articleCount;

    /**专栏点赞数 */
    private Integer praiseCount;

    /** 专栏被阅读数 */
    private Integer readCount;

    /** 专栏被收藏数 */
    private Integer collectionCount;

    /** 专栏评论数 */
    private Integer commentCount;
}
