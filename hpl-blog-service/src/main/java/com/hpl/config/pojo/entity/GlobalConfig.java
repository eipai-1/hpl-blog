package com.hpl.config.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/7/8 10:45
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("global_conf")
public class GlobalConfig extends CommonEntity {
    private static final long serialVersionUID = -6122208316544171301L;

    // 配置项名称
    @TableField("`key`")
    private String key;
    // 配置项值
    private String value;
    // 备注
    private String comment;
    // 删除
    private Integer deleted;
}