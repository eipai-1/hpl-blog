package com.hpl.config.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/7/8 9:56
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("config")
public class Config extends CommonEntity {
    private static final long serialVersionUID = -6122208316544171303L;

    /** 类型 */
    private Integer type;

    /** 名称 */
    @TableField("`name`")
    private String name;

    /** 图片链接 */
    private String bannerUrl;

    /** 跳转链接 */
    private String jumpUrl;

    /** 内容 */
    private String content;

    /** 排序 */
    @TableField("`rank`")
    private Integer rank;

    /** 状态：0-未发布，1-已发布 */
    private Integer status;

    /** 0未删除 1 已删除 */
    private Integer deleted;

    /** 配置对应的标签，英文逗号分隔 */
    private String tags;

    /** 扩展信息，如记录 评分，阅读人数，下载次数等 */
    private String extra;
}
