package com.hpl.rabbitmq;

import lombok.Getter;

/**
 * @author : rbe
 * @date : 2024/9/7 10:53
 */
@Getter
public enum RabbitQueueEnum {

    ARTICLE_INSERT("article_insert_update","新增or更新文章队列"),
    ARTICLE_DELETE("article_delete","删除文章队列")
    ;

    private final String name;
    private final String desc;

    RabbitQueueEnum(String name, String desc){
        this.name = name;
        this.desc = desc;
    }
}
