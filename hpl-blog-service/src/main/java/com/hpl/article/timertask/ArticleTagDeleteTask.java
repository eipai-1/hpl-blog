package com.hpl.article.timertask;

import com.hpl.article.service.ArticleTagService;
import com.hpl.xxljob.CommonTimerTaskRunner;
import com.xxl.job.core.handler.annotation.XxlJob;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author : rbe
 * @date : 2024/9/7 16:33
 */
@Component
@Slf4j
public class ArticleTagDeleteTask implements CommonTimerTaskRunner {

    @Resource
    private ArticleTagService articleTagService;

    @Override
    @XxlJob("articleTagDeleteTask")
    public void run() {
        log.info("开始执行定时任务：删除文章标签");
        articleTagService.handleDeleteArticleTags();
    }
}
