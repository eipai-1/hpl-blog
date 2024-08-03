package com.hpl.column.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/3 18:09
 */

@Data
public class ColumnListDTO {

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
     * 作者信息
     *********************/

    /** 作者 */
    private Long authorId;

    /** 作者名 */
    private String authorName;

    /** 作者头像 */
    private String authorAvatar;


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
