package com.hpl.notify.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.global.comtext.ReqInfoContext;
import com.hpl.notify.pojo.dtos.NotifyMsgDTO;
import com.hpl.notify.pojo.entity.NotifyMsg;
import com.hpl.notify.pojo.enums.NotifyStateEnum;
import com.hpl.notify.mapper.NotifyMsgMapper;
import com.hpl.notify.pojo.enums.NotifyTypeEnum;
import com.hpl.notify.service.NotifyMagService;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.user.pojo.entity.UserFoot;
import com.hpl.user.service.UserRelationService;
import com.hpl.util.NumUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/1 8:43
 */
@Service
public class NotifyMagServiceImpl extends ServiceImpl<NotifyMsgMapper,NotifyMsg> implements NotifyMagService {

    @Autowired
    NotifyMsgMapper notifyMsgMapper;

    @Autowired
    UserRelationService userRelationService;

    /**
     * 查询用户未读消息数量
     *
     * @param userId
     * @return
     */
    @Override
    public int queryUserNotifyMsgCount(Long userId){

        //todo 这个有效果吗，我没试过这种写法
        return lambdaQuery()
                .eq(NotifyMsg::getNotifyUserId, userId)
                .eq(NotifyMsg::getState, NotifyStateEnum.UNREAD.getState())
                .count().intValue();
    }

    /**
     * 根据用户ID、通知类型和分页参数，查询用户的的通知列表。
     * 支持的类型包括回复、评论、收藏和点赞。
     * 如果指定的通知类型不支持，将返回所有类型的通知。
     * 该方法还会将已读状态更新为已读，并根据通知列表更新用户的全局消息数和关注状态。
     *
     * @param userId 用户ID。
     * @param type 通知类型。
     * @param pageParam 分页参数。
     * @return 返回通知的分页列表。
     */
    @Override
    public CommonPageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, CommonPageParam pageParam) {
        // 初始化通知列表
        List<NotifyMsgDTO> list = new ArrayList<>();

        // 根据通知类型查询特定类型的通知，如果不支持则查询所有类型的通知
        switch (type) {
            case REPLY:
            case COMMENT:
            case COLLECT:
            case PRAISE:
                list =  notifyMsgMapper.listArticleRelatedNotices(userId, type.getType(), pageParam);
                break;
            default:
                list =  notifyMsgMapper.listNormalNotices(userId, type.getType(), pageParam);
                break;
        }

        // 如果通知列表为空，返回空的分页列表
        if (CollectionUtils.isEmpty(list)) {
            return CommonPageListVo.emptyVo();
        }

        // 筛选出未读通知的ID
        // 设置消息为已读状态
        List<Long> ids = list.stream()
                .filter(s -> s.getState() == NotifyStateEnum.UNREAD.getState())
                .map(NotifyMsgDTO::getMsgId)
                .collect(Collectors.toList());

        // 如果有未读通知，更新通知的已读状态
        if (!ids.isEmpty()) {
            notifyMsgMapper.updateNoticeRead(ids);
        }

        // 更新用户的全局消息数
        // 更新全局总的消息数
        ReqInfoContext.getReqInfo().setMsgNum(queryUserNotifyMsgCount(userId));
        // 更新用户关注状态
        // 更新当前登录用户对粉丝的关注状态
        updateFollowStatus(userId, list);

        // 返回通知的分页列表
        return CommonPageListVo.newVo(list, pageParam.getPageSize());
    }


    private void updateFollowStatus(Long userId, List<NotifyMsgDTO> list) {
        List<Long> targetUserIds = list.stream().filter(s -> s.getType() == NotifyTypeEnum.FOLLOW.getType()).map(NotifyMsgDTO::getOperateUserId).collect(Collectors.toList());
        if (targetUserIds.isEmpty()) {
            return;
        }

        // 查询userId已经关注过的用户列表；并将对应的msg设置为true，表示已经关注过了；不需要再关注
        Set<Long> followedUserIds = userRelationService.getFollowedUserId(targetUserIds, userId);
        list.forEach(notify -> {
            if (followedUserIds.contains(notify.getOperateUserId())) {
                notify.setMsg("true");
            } else {
                notify.setMsg("false");
            }
        });
    }




    /**
     * 查询指定用户未读消息的类型和数量。
     *
     * @param userId 用户ID，用于查询该用户未读消息的情况。
     * @return 返回一个映射，其中键是消息类型，值是未读消息的数量。如果某种类型的消息不存在，则数量为0。
     */
    @Override
    public Map<String, Integer> queryUnreadCounts(long userId) {
        // 初始化一个空的映射，用于存储消息类型和未读数量。
        Map<Integer, Integer> map = Collections.emptyMap();

        // 如果当前请求信息不为空且消息数量大于0，则进行查询。
        if (ReqInfoContext.getReqInfo() != null && NumUtil.gtZero(ReqInfoContext.getReqInfo().getMsgNum())) {
            // 创建查询包装器，用于构建SQL查询条件。
            QueryWrapper<NotifyMsg> wrapper = new QueryWrapper<>();
            // 选择查询的列，即消息类型和未读消息的数量。
            wrapper.select("type, count(*) as cnt");
            // 筛选条件，查询指定用户的未读消息。
            wrapper.eq("notify_user_id", userId);
            // 如果未读状态的值不为空，则添加状态筛选条件。
            if (NotifyStateEnum.UNREAD.getState() != null) {
                wrapper.eq("state", NotifyStateEnum.UNREAD.getState());
            }
            // 按消息类型分组，以统计每种类型的消息数量。
            wrapper.groupBy("type");
            // 执行查询，并将结果存储在一个列表中。
            List<Map<String, Object>> innerMap = listMaps(wrapper);
            // 遍历查询结果，将消息类型和数量添加到映射中。
            innerMap.forEach(s -> {
                map.put(Integer.valueOf(s.get("type").toString()), Integer.valueOf(s.get("cnt").toString()));
            });
        }

        // 初始化一个有序映射，用于存储最终的结果，保证消息类型的顺序。
        // 指定先后顺序
        Map<String, Integer> ans = new LinkedHashMap<>();
        // 对于每种消息类型，如果在之前的映射中存在，则将其添加到结果映射中，否则添加一个值为0的条目。
        initCnt(NotifyTypeEnum.COMMENT, map, ans);
        initCnt(NotifyTypeEnum.REPLY, map, ans);
        initCnt(NotifyTypeEnum.PRAISE, map, ans);
        initCnt(NotifyTypeEnum.COLLECT, map, ans);
        initCnt(NotifyTypeEnum.FOLLOW, map, ans);
        initCnt(NotifyTypeEnum.SYSTEM, map, ans);

        // 返回最终的结果映射。
        return ans;
    }


    private void initCnt(NotifyTypeEnum type, Map<Integer, Integer> map, Map<String, Integer> result) {
        result.put(type.name().toLowerCase(), map.getOrDefault(type.getType(), 0));
    }

    @Override
    public void saveArticleNotify(UserFoot userFoot, NotifyTypeEnum notifyTypeEnum) {
        NotifyMsg msg = new NotifyMsg().setRelatedId(userFoot.getDocumentId())
                .setNotifyUserId(userFoot.getDocumentUserId())
                .setOperateUserId(userFoot.getUserId())
                .setType(notifyTypeEnum.getType() )
                .setState(NotifyStateEnum.UNREAD.getState())
                .setMsg("");
        NotifyMsg record = new NotifyMsg();
        List<NotifyMsg> list = lambdaQuery().eq(NotifyMsg::getNotifyUserId, msg.getNotifyUserId())
                .eq(NotifyMsg::getOperateUserId, msg.getOperateUserId())
                .eq(NotifyMsg::getType, msg.getType())
                .eq(NotifyMsg::getRelatedId, msg.getRelatedId())
                .page(new Page<>(0, 1))
                .getRecords();
        if (!CollectionUtils.isEmpty(list)) {
            record = list.get(0);
        }


        if (record == null) {
            // 若之前已经有对应的通知，则不重复记录；因为一个用户对一篇文章，可以重复的点赞、取消点赞，但是最终我们只通知一次
            this.save(msg);
        }
    }
}
