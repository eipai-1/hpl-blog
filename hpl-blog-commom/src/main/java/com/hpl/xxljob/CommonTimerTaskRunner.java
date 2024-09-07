package com.hpl.xxljob;

/**
 * 定时执行器，定时器都要实现本接口，并需要把实现类加入到spring容器中
 *
 * @author : rbe
 * @date : 2024/6/29 17:20
 */
public interface CommonTimerTaskRunner {

    /**
     * 任务执行的具体内容
     */
    void run();
}
