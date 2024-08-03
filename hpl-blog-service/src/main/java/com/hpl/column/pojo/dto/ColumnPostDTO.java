package com.hpl.column.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/7 9:08
 */
@Data
public class ColumnPostDTO implements Serializable {

    /** 专栏名 */
    private String columnName;

    /** 作者 */
    private Long authorId;

    /** 简介 */
    private String introduction;

    /** 封面 */
    private String cover;

    /** 排序 */
    private Integer section;

}

