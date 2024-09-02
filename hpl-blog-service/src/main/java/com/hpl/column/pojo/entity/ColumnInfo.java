package com.hpl.column.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("column_info")
public class ColumnInfo extends CommonEntity {

    private static final long serialVersionUID = 1920830534262012026L;

    /** 专栏名 */
    @TableField("column_name")
    private String columnName;

    /** 作者id */
    @TableField("author_id")
    private Long authorId;

    /** 简介 */
    @TableField("introduction")
    private String introduction;

    /** 排序 */
    @TableField("section")
    private Integer section;

    /** 是否删除 */
    private Integer deleted;

}
