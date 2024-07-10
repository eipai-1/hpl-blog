package com.hpl.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.pojo.CommonPageParam;
import com.hpl.user.pojo.dto.FollowUserInfoDTO;
import com.hpl.user.pojo.entity.UserRelation;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/6/29 19:24
 */
@Mapper
public interface UserRelationMapper extends BaseMapper<UserRelation> {

    List<FollowUserInfoDTO> queryUserFollowList(Long userId, CommonPageParam pageParam);
}
