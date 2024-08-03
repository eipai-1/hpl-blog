package com.hpl.statistic.pojo.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.hpl.pojo.CommonEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @author : rbe
 * @date : 2024/8/2 18:07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("trace_count")
public class TraceCount extends CommonEntity {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private Long articleId;

    private Integer collectionState;

    private Integer commentState;

    private Integer praiseState;

}