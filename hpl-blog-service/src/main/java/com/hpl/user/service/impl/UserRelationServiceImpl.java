package com.hpl.user.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.user.pojo.entity.UserRelation;
import com.hpl.user.mapper.UserRelationMapper;
import com.hpl.user.service.UserRelationService;
import org.springframework.stereotype.Service;


/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
@Service
public class UserRelationServiceImpl extends ServiceImpl<UserRelationMapper, UserRelation> implements UserRelationService {
}
