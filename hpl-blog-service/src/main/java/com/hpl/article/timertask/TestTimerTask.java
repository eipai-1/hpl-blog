package com.hpl.article.timertask;

import com.hpl.xxljob.CommonTimerTaskRunner;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/7 14:57
 */
@Slf4j
@Component
public class TestTimerTask implements CommonTimerTaskRunner {

    @Override
    @XxlJob("testTimerTask")
    public void run() {
        log.info("test timer task run");
    }
}
