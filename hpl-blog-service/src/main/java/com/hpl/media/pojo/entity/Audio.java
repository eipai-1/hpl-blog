package com.hpl.media.pojo.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/7/30 13:33
 */
@Data
@TableName("audio")
public class Audio implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 文件名称
     */
    private String audioName;

    /**
     * 文件拓展类型
     */
    private String audioExtend;


    /**
     * minio存储访问地址
     */
    private String urlMinio;

    /**
     * 是否删除 1-删除 0-未删除
     */
    private Integer deleted;

    /**
     * 上传时间
     */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /**
     * 修改时间
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;


    /**
     * 备注
     */
    private String remark;

    /**
     * 文件大小
     */
    private Long fileSize;

}
