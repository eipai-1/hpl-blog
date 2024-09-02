package com.hpl.article.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 标签管理表
 *
 * @author louzai
 * @date 2022-07-18
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tag")
public class Tag extends CommonEntity {

    private static final long serialVersionUID = 3796460143933607644L;

    /** 标签名称 */
    private String tagName;

    /** 状态：0-未发布，1-已发布 */
    private Integer status;

    /** 是否删除 */
    private Integer deleted;
}
