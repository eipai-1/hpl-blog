package com.hpl.statistic.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/9 12:01
 */
@Data
public class StatisticsDayDTO {

    /** 日期 */
    private String date;

    /** 数量 */
    private Long pvCount;

    /** UV数量 */
    private Long uvCount;
}
