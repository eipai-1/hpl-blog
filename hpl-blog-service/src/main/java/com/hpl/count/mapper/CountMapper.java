package com.hpl.count.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.count.pojo.entity.Count;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/9/1 22:23
 */
@Mapper
public interface CountMapper extends BaseMapper<Count> {
}
