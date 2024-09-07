package com.hpl.article.consumer;

import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.vo.ArticleListDTO;
import com.hpl.article.service.ArticleService;
import com.hpl.rabbitmq.RabbitQueueEnum;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @author : rbe
 * @date : 2024/9/7 11:01
 */
@Component
public class ArticleInsertConsumer {

    @Resource
    private ArticleService articleService;

    private String key = RabbitQueueEnum.ARTICLE_INSERT.getName();

    @RabbitListener(queues = "article_insert")
    public void handleMessage(String message) throws IOException {
        Long articleId = Long.parseLong(message);
        articleService.loadArticleToEs(articleId);
    }
}
