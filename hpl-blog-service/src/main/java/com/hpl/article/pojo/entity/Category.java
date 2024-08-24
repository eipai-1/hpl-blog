package com.hpl.article.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;


import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/8/20 10:55
 */
@Data
@TableName("category")
public class Category implements Serializable {

    /** 类目ID */
    private String id;

    /** 类目名称 */
    private String categoryName;

    /** 父类目ID */
    private String parentId;

    /** 状态：0-未发布，1-已发布 */
    private Integer status;

    /**  排序 */
    @TableField("`rank`")
    private Integer rank;

    private Integer isLeaf;


}
