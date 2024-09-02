package com.hpl.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.statistic.pojo.dto.StatisticsDayDTO;
import com.hpl.statistic.pojo.entity.RequestCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/9 10:51
 */
@Mapper
public interface RequestCountMapper extends BaseMapper<RequestCount> {
    @Select("select sum(cnt) from request_count")
    Long getPvTotalCount();

    /**
     * 获取 PV UV 数据列表
     * @param day
     * @return
     */
    List<StatisticsDayDTO> getPvUvDayList(@Param("day") Integer day);
}
