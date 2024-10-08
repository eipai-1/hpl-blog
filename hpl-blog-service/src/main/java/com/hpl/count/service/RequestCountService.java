package com.hpl.count.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.count.pojo.dto.StatisticsDayDTO;
import com.hpl.count.pojo.entity.RequestCount;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/9 10:54
 */
public interface RequestCountService extends IService<RequestCount> {
    RequestCount getRequestCount(String host);

    void insert(String host);

    void incrementCount(Long id);

    Long getPvTotalCount();

    List<StatisticsDayDTO> getPvUvDayList(Integer day);
}
