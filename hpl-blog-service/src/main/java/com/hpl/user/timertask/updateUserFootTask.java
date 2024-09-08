package com.hpl.user.timertask;

import com.hpl.user.service.UserFootService;
import com.hpl.xxljob.CommonTimerTaskRunner;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/8 9:45
 */
@Component
public class updateUserFootTask implements CommonTimerTaskRunner {

    @Resource
    private UserFootService userFootService;

    @Override
    @XxlJob("updateUserFootTask")
    public void run(){
        userFootService.handleUpdateUserFoot();
    }
}
