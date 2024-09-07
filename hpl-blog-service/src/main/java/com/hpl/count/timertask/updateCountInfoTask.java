package com.hpl.count.timertask;

import com.hpl.count.service.CountService;
import com.hpl.xxljob.CommonTimerTaskRunner;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/7 17:16
 */
@Component
public class updateCountInfoTask implements CommonTimerTaskRunner {

    @Resource
    private CountService countService;

    @Override
    @XxlJob("updateCountInfoTask")
    public void run() {
        countService.handleUpdateCountInfo();
    }
}
