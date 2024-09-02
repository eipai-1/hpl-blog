package com.hpl.statistic.service;

import com.hpl.statistic.pojo.dto.StatisticsCountDTO;
import com.hpl.statistic.pojo.dto.StatisticsDayDTO;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/9 10:54
 */
public interface StatisticSettingService {

    /**
     * 保存计数
     *
     * @param host
     */
    void saveRequestCount(String host);

    /**
     * 获取总数
     *
     * @return
     */
    StatisticsCountDTO getStatisticsCount();

    /**
     * 获取每天的PV UV统计数据
     *
     * @param day
     * @return
     */
    List<StatisticsDayDTO> getPvUvDayList(Integer day);
}
