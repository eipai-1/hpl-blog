package com.hpl.article.service.impl;


import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.hpl.annotation.permission.UserRole;
import com.hpl.article.enent.ArticleMsgEvent;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.mapper.ArticleDetailMapper;
import com.hpl.article.mapper.ArticleMapper;
import com.hpl.article.mapper.ArticleTagMapper;
import com.hpl.article.pojo.enums.*;
import com.hpl.article.service.ArticleWriteService;
import com.hpl.enums.StatusEnum;
import com.hpl.global.context.ReqInfoContext;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserFootService;
import com.hpl.util.ExceptionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 文章操作相关服务类
 *
 * @author louzai
 * @date 2022-07-20
 */
@Slf4j
@Service
public class ArticleWriteServiceImpl implements ArticleWriteService {

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private ArticleTagMapper articleTagMapper;

    @Autowired
    private ArticleDetailMapper articleDetailMapper;

    @Autowired
    private UserFootService userFootService;

//    @Autowired
//    private ImageService imageService;


//    @Autowired
//    private AuthorWhiteListService articleWhiteListService;



    /**
     * 删除文章
     *
     * @param articleId
     */
    @Override
    public void deleteArticle(Long articleId, Long loginUserId) {
        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Article::getId, articleId)
                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode());

        Article article = articleMapper.selectOne(wrapper);

        if (article != null && !Objects.equals(article.getAuthorId(), loginUserId)) {
            // 没有权限
            throw ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED, "请确认文章是否属于您!");
        }

        if (article != null && article.getDeleted() != CommonDeletedEnum.YES.getCode()) {
            article.setDeleted(CommonDeletedEnum.YES.getCode());
            articleMapper.updateById(article);

            // 发布文章删除事件
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.DELETE, article));
        }
    }


    /**
     * 非白名单的用户，发布的文章需要先进行审核
     *
     * @param article
     * @return
     */
    private boolean needToReview(Article article) {
        // 把 admin 用户加入白名单
        UserInfo user = ReqInfoContext.getReqInfo().getUserInfo();
        if (user.getUserRole() != null && user.getUserRole().equals(UserRole.ADMIN.getCode())) {
            return false;
        }
//        return article.getStatus() == PushStatusEnum.ONLINE.getCode() && !articleWhiteListService.authorInArticleWhiteList(article.getAuthorId());
        //todo
        return article.getStatus() == PublishStatusEnum.PUBLISHED.getCode();
    }

    @Override
    public void updateById(Article article){
        articleMapper.updateById(article);
    }
}
