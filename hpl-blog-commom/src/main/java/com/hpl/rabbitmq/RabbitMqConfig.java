package com.hpl.rabbitmq;


import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author : rbe
 * @date : 2024/9/7 10:24
 */
@Configuration
public class RabbitMqConfig {

    @Bean
    public Queue queueArticleInsert() {
        return new Queue(RabbitQueueEnum.ARTICLE_INSERT.getName());
    }

    @Bean
    public Queue queueArticleDelete() {
        return new Queue(RabbitQueueEnum.ARTICLE_DELETE.getName());
    }

}
