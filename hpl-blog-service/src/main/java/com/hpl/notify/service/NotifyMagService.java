package com.hpl.notify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.notify.pojo.dtos.NotifyMsgDTO;
import com.hpl.notify.pojo.entity.NotifyMsg;
import com.hpl.notify.pojo.enums.NotifyTypeEnum;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.user.pojo.entity.UserFoot;

import java.util.Map;

/**
 * @author : rbe
 * @date : 2024/7/1 8:43
 */
public interface NotifyMagService extends IService<NotifyMsg> {

    /**
     * 查询用户未读消息数量
     *
     * @param userId
     * @return
     */
    int queryUserNotifyMsgCount(Long userId);

    /**
     * 查询通知列表
     *
     * @param userId
     * @param type
     * @param pageParam
     * @return
     */
    CommonPageListVo<NotifyMsgDTO> queryUserNotices(Long userId, NotifyTypeEnum type, CommonPageParam pageParam);

    /**
     * 查询未读消息数
     * @param userId
     * @return
     */
    Map<String, Integer> queryUnreadCounts(long userId);

    /**
     * 保存通知
     *
     * @param userFoot
     * @param notifyTypeEnum
     */
    void saveArticleNotify(UserFoot userFoot, NotifyTypeEnum notifyTypeEnum);
}
