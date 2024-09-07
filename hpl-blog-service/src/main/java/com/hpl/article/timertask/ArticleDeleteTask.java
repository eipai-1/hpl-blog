package com.hpl.article.timertask;

import com.hpl.article.service.ArticleService;
import com.hpl.xxljob.CommonTimerTaskRunner;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/7 15:37
 */
@Component
@Slf4j
public class ArticleDeleteTask implements CommonTimerTaskRunner {

    @Resource
    private ArticleService articleService;

    @Override
    @XxlJob("articleDeleteTask")
    public void run() {
        log.info("开始执行定时任务：删除文章");
        articleService.handleDeleteArticle();
    }
}
