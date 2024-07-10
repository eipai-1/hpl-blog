package com.hpl.config.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.config.pojo.entity.Config;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/7/8 10:00
 */
@Mapper
public interface ConfigMapper extends BaseMapper<Config> {
}
