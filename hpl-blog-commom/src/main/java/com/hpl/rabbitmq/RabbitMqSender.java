package com.hpl.rabbitmq;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/7 10:30
 * @Description 生产者（发送消息）
 */
@Component
public class RabbitMqSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    public void sengMessage(String message) {
        System.out.println("发送祝福："+message);
        rabbitTemplate.convertAndSend("simple.hello",message);
    }
}
