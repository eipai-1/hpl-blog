package com.hpl.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.statistic.pojo.dto.StatisticsDayDTO;
import com.hpl.statistic.pojo.entity.ReadCount;
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
public interface ReadCountMapper extends BaseMapper<ReadCount> {

}
