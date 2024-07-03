package com.hpl.notify.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.notify.pojo.entity.NotifyMsg;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/7/1 8:53
 */
@Mapper
public interface NotifyMapper extends BaseMapper<NotifyMsg> {
}
