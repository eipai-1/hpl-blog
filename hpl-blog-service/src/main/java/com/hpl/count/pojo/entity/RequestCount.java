package com.hpl.count.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Date;

/**
 * @author : rbe
 * @date : 2024/7/9 10:49
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("request_count")
public class RequestCount extends CommonEntity {

    private static final long serialVersionUID = 1L;

    /** 机器IP */
    private String host;

    /** 访问计数 */
    private Integer cnt;

    /** 当前日期 */
    private Date date;
}
