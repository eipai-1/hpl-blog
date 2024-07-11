package com.hpl.article.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 类目管理表
 *
 * @author louzai
 * @date 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("category")
public class Category extends CommonEntity {

    private static final long serialVersionUID = 1L;

    /** 类目名称 */
    private String categoryName;

    /** 状态：0-未发布，1-已发布 */
    private Integer status;

    /**  排序 */
    @TableField("`rank`")
    private Integer rank;

    /**  删除状态：0-未删除，1-已删除 */
    private Integer deleted;
}
