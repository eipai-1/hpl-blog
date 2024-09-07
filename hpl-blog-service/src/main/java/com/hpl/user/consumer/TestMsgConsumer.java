package com.hpl.user.consumer;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/7 10:36
 */
@Component
public class TestMsgConsumer {

    @RabbitListener(queues = "simple.hello")
    public void handleMessage(String message) {
        System.out.println("我收到了你的祝福："+message);
    }
}
