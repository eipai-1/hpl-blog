package com.hpl.global.listener;

import cn.hutool.extra.spring.SpringUtil;
import com.hpl.count.service.StatisticUserService;

import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * @author : rbe
 * @date : 2024/6/30 18:03
 */
@WebListener
public class OnlineUserCountListener implements HttpSessionListener {


    /**
     * 当新的会话创建时，此方法被调用。用于统计在线用户数量。
     * 通过继承HttpSessionListener并重写sessionCreated方法，可以在会话创建时执行特定逻辑。
     * 此方法的核心功能是增加在线用户计数。
     *
     * @param se HttpSessionEvent对象，提供关于会话创建事件的信息。
     */
    @Override
    public void sessionCreated(HttpSessionEvent se) {

        // 调用HttpSessionListener的sessionCreated方法，以确保符合HttpSessionListener的规范。
        HttpSessionListener.super.sessionCreated(se);

        // 增加在线用户计数。利用SpringUtil获取UserStatisticService实例，并调用其incrOnlineUserCnt方法。
        // 这里传入的参数1表示增加1个在线用户。
        SpringUtil.getBean(StatisticUserService.class)
                .incrOnlineUserCnt(1);
    }


    /**
     * 当会话销毁时调用此方法。会话销毁可能发生在用户关闭浏览器、会话超时或其他原因导致会话结束的情况下。
     * 此方法的主要作用是更新在线用户统计信息，由于会话销毁代表一个用户下线，因此需要减少在线用户计数。
     *
     * @param se HttpSessionEvent 对象，提供了关于 HttpSession 事件的信息，例如事件发生的时间、受影响的会话等。
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        // 调用超类方法，以确保符合HttpSessionListener接口的约定。
        HttpSessionListener.super.sessionDestroyed(se);

        // 通过SpringUtil获取UserStatisticService的实例，并减少在线用户计数。
        // 使用-1是因为一个会话的销毁代表一个在线用户的减少。
        SpringUtil.getBean(StatisticUserService.class)
                .incrOnlineUserCnt(-1);
    }

}
