package com.hpl.notify.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hpl.notify.pojo.entity.NotifyMsg;
import com.hpl.notify.pojo.enums.NotifyStateEnum;
import com.hpl.notify.mapper.NotifyMapper;
import com.hpl.notify.service.NotifyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author : rbe
 * @date : 2024/7/1 8:43
 */
@Service
public class NotifyServiceImpl extends ServiceImpl<NotifyMapper,NotifyMsg> implements NotifyService {

    @Autowired
    NotifyMapper notifyMapper;

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
}
