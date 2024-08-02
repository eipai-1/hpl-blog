package com.hpl.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.user.pojo.dto.FollowUserInfoDTO;
import com.hpl.user.pojo.entity.UserRelation;

import java.util.List;
import java.util.Set;

/**
 * @author : rbe
 * @date : 2024/6/29 19:25
 */
public interface UserRelationService extends IService<UserRelation> {

    /**
     * 根据登录用户从给定用户列表中，找出已关注的用户id
     *
     * @param userIds
     * @param loginUserId
     * @return
     */
    Set<Long> getFollowedUserId(List<Long> userIds, Long loginUserId);

    /**
     * 我关注的用户
     *
     * @param userId
     * @param pageParam
     * @return
     */
    CommonPageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, CommonPageParam pageParam);


    /**
     * 获取当前用户关注人数
     * @param userId
     * @return
     */
    Long queryUserFollowCount(Long userId);


    /**
     * 获取用户关注数
     * @param userId
     * @return
     */
    Long queryUserFansCount(Long userId);

    /**
     * 根据fanId判断是否关注userId
     * @param userId
     * @param fansUserId
     * @return
     */
    Boolean isFollow(Long userId, Long fansUserId);


}
