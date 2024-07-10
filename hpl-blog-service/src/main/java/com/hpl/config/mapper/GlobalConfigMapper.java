package com.hpl.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.config.pojo.entity.GlobalConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/7/8 10:48
 */
@Mapper
public interface GlobalConfigMapper extends BaseMapper<GlobalConfig> {
}
