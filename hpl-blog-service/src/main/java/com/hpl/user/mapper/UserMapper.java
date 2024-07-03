package com.hpl.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.user.pojo.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/6/29 19:24
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
