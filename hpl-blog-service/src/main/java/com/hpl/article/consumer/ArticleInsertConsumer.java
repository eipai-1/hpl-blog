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


//    定时器
    @RabbitListener(queues = "article_insert_update")
    public void loadArticleToEs(String message) throws IOException {
        Long articleId = Long.parseLong(message);
        articleService.loadArticleToEs(articleId);
    }

    @RabbitListener(queues = "article_delete")
    public void deleteArticleToEs(String message) {
        Long articleId = Long.parseLong(message);
        articleService.deleteArticleToEs(articleId);
    }
}
