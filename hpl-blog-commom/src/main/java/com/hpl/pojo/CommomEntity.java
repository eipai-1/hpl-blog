package com.hpl.pojo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * @author : rbe
 * @date : 2024/6/29 15:50
 */
@Data
public class CommomEntity implements Serializable {

    /** 主键id */
    @TableId(type=IdType.AUTO)
    private Long id;

    /** 创建时间 */
    private Date createTime;

    /** 更新时间 */
    private Date updateTime;
}
