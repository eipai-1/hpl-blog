package com.hpl.statistic.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.statistic.pojo.entity.ReadCount;
import com.hpl.statistic.pojo.entity.TraceCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/7/9 10:51
 */
@Mapper
public interface TraceCountMapper extends BaseMapper<TraceCount> {

}
