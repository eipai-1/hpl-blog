package com.hpl.column.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/7 9:08
 */
@Data
public class ColumnEditDTO implements Serializable {

    /** 专栏id */
    private Long columnId;

    /** 专栏名 */
    private String columnName;

    /** 简介 */
    private String introduction;

    /** 排序 */
    private Integer section;

}

