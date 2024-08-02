package com.hpl.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.user.mapper.UserRelationMapper;
import com.hpl.user.pojo.dto.FollowUserInfoDTO;
import com.hpl.user.pojo.entity.UserRelation;
import com.hpl.user.pojo.enums.FollowStateEnum;
import com.hpl.user.service.UserRelationService;
import com.hpl.util.MapUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
@Service
public class UserRelationServiceImpl extends ServiceImpl<UserRelationMapper, UserRelation> implements UserRelationService {

    @Autowired
    UserRelationMapper userRelationMapper;

    /**
     * 查询用户的关注列表
     *
     * @param userId
     * @param pageParam
     * @return
     */
    @Override
    public CommonPageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, CommonPageParam pageParam) {
        List<FollowUserInfoDTO> userRelationList = userRelationMapper.queryUserFollowList(userId, pageParam);
        return CommonPageListVo.newVo(userRelationList, pageParam.getPageSize());
    }


    /**
     * 根据登录用户从给定用户列表中，找出已关注的用户id
     *
     * @param userIds
     * @param fansUserId
     * @return
     */
    @Override
    public Set<Long> getFollowedUserId(List<Long> userIds, Long fansUserId){
        if (CollectionUtils.isEmpty(userIds)) {
            return Collections.emptySet();
        }

        LambdaQueryWrapper<UserRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserRelation::getFollowUserId, fansUserId)
                .in(UserRelation::getUserId, userIds);

        List<UserRelation> relationList = userRelationMapper.selectList(wrapper);

        Map<Long, UserRelation> relationMap = MapUtil.toMap(relationList, UserRelation::getUserId, r -> r);
        return relationMap.values().stream()
                .filter(s -> s.getFollowState().equals(FollowStateEnum.FOLLOW.getCode()))
                .map(UserRelation::getUserId)
                .collect(Collectors.toSet());

    }

    @Override
    public Long queryUserFollowCount(Long userId){
        return lambdaQuery()
                .eq(UserRelation::getFollowUserId, userId)
                .eq(UserRelation::getFollowState, FollowStateEnum.FOLLOW.getCode())
                .count();
    }

    @Override
    public Long queryUserFansCount(Long userId){
        return lambdaQuery()
                .eq(UserRelation::getUserId, userId)
                .eq(UserRelation::getFollowState, FollowStateEnum.FOLLOW.getCode())
                .count();
    }

    /**
     * 根据fanId判断是否关注userId
     * @param userId
     * @param fansUserId
     * @return
     */
    @Override
    public Boolean isFollow(Long userId, Long fansUserId){
        return lambdaQuery()
                .eq(UserRelation::getUserId, userId)
                .eq(UserRelation::getFollowUserId, fansUserId)
                .eq(UserRelation::getFollowState, FollowStateEnum.FOLLOW.getCode())
                .count() > 0;
    }
}
