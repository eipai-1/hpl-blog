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
     * 我关注的用户
     *
     * @param userId
     * @param pageParam
     * @return
     */
    CommonPageListVo<FollowUserInfoDTO> getUserFollowList(Long userId, CommonPageParam pageParam);



    /**
     * 根据登录用户从给定用户列表中，找出已关注的用户id
     *
     * @param userIds
     * @param loginUserId
     * @return
     */
    Set<Long> getFollowedUserId(List<Long> userIds, Long loginUserId);


    Long queryUserFollowCount(Long userId);


    Long queryUserFansCount(Long userId);


}
