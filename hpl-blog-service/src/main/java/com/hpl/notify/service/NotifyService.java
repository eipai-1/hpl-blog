package com.hpl.notify.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.notify.pojo.entity.NotifyMsg;

/**
 * @author : rbe
 * @date : 2024/7/1 8:43
 */
public interface NotifyService extends IService<NotifyMsg> {

    /**
     * 查询用户未读消息数量
     *
     * @param userId
     * @return
     */
    int queryUserNotifyMsgCount(Long userId);
}
