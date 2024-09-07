package com.hpl.rabbitmq;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/9/7 10:53
 */
@Getter
public enum RabbitQueueEnum {

    ARTICLE_INSERT("article_insert","新增文章队列"),
    ;

    private final String name;
    private final String desc;

    RabbitQueueEnum(String name, String desc){
        this.name = name;
        this.desc = desc;
    }
}
