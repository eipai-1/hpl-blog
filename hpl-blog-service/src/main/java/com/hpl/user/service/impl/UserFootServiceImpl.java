package com.hpl.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.user.pojo.entity.UserFoot;
import com.hpl.user.mapper.UserFootMapper;
import com.hpl.user.service.UserFootService;
import org.springframework.stereotype.Service;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
@Service
public class UserFootServiceImpl extends ServiceImpl<UserFootMapper, UserFoot> implements UserFootService {
}
