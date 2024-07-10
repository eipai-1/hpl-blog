package com.hpl.article.service.impl;


import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.hpl.annotation.permission.UserRole;
import com.hpl.article.dto.ArticlePostDTO;
import com.hpl.article.enent.ArticleMsgEvent;
import com.hpl.article.entity.Article;
import com.hpl.article.entity.ArticleDetail;
import com.hpl.article.entity.ArticleTag;
import com.hpl.article.enums.*;
import com.hpl.article.mapper.ArticleDetailMapper;
import com.hpl.article.mapper.ArticleMapper;
import com.hpl.article.mapper.ArticleTagMapper;
import com.hpl.article.service.ArticleWriteService;
import com.hpl.article.service.ColumnSettingService;
import com.hpl.enums.StatusEnum;
import com.hpl.global.comtext.ReqInfoContext;
import com.hpl.pojo.CommonDeletedEnum;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserFootService;
import com.hpl.util.ExceptionUtil;
import com.hpl.util.IdUtil;
import com.hpl.util.NumUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

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
    private ColumnSettingService columnSettingService;

    @Autowired
    private UserFootService userFootService;

//    @Autowired
//    private ImageService imageService;

    @Resource
    private TransactionTemplate transactionTemplate;

//    @Autowired
//    private AuthorWhiteListService articleWhiteListService;


    /**
     * 保存文章，当articleId存在时，表示更新记录； 不存在时，表示插入
     *
     * @param articlePostDTO
     * @return
     */
    @Override
    public Long saveArticle(ArticlePostDTO articlePostDTO, Long author) {

        Article article = new Article();
        // 设置作者ID
        article.setAuthorId(author);
        article.setId(articlePostDTO.getArticleId());
        article.setTitle(articlePostDTO.getTitle());
        article.setShortTitle(articlePostDTO.getShortTitle());
        article.setArticleType(ArticleTypeEnum.valueOf(articlePostDTO.getArticleType().toUpperCase()).getCode());
        article.setPicture(articlePostDTO.getCover() == null ? "" : articlePostDTO.getCover());
        article.setCategoryId(articlePostDTO.getCategoryId());
        article.setSource(articlePostDTO.getSource());
        article.setSourceUrl(articlePostDTO.getSourceUrl());
        article.setSummary(articlePostDTO.getSummary());
        article.setStatus(articlePostDTO.pushStatus().getCode());
        article.setDeleted(articlePostDTO.deleted() ? CommonDeletedEnum.YES.getCode() : CommonDeletedEnum.NO.getCode());


//        String content = imageService.mdImgReplace(articlePostDTO.getContent());
        String content = articlePostDTO.getContent();       //todo 为实现image服务，暂时不替换图片

        return transactionTemplate.execute(new TransactionCallback<Long>() {
            @Override
            public Long doInTransaction(TransactionStatus status) {
                Long articleId;
                if (NumUtil.eqZero(articlePostDTO.getArticleId())) {
                    articleId = insertArticle(article, content, articlePostDTO.getTagIds());
                    log.info("文章发布成功! title={}", articlePostDTO.getTitle());
                } else {
                    articleId = updateArticle(article, content, articlePostDTO.getTagIds());
                    log.info("文章更新成功！ title={}", article.getTitle());
                }
                if (articlePostDTO.getColumnId() != null) {
                    // 更新文章对应的专栏信息
                    columnSettingService.saveColumnArticle(articleId, articlePostDTO.getColumnId());
                }
                return articleId;
            }
        });
    }

    /**
     * 新建文章
     *
     * @param article
     * @param content
     * @param tags
     * @return
     */
    private Long insertArticle(Article article, String content, Set<Long> tags) {
        // article + article_detail + tag  三张表的数据变更
        if (needToReview(article)) {
            // 非白名单中的作者发布文章需要进行审核
            article.setStatus(PushStatusEnum.REVIEW.getCode());
        }

        // 1. 保存文章
        // 使用分布式id生成文章主键
        Long articleId = IdUtil.genId();
        article.setId(articleId);
        if (articleId == null){
            articleMapper.insert(article);
        }else {
            articleMapper.updateById(article);
        }

        // 2. 保存文章内容
        ArticleDetail detail = new ArticleDetail();
        detail.setArticleId(articleId);
        detail.setContent(content);
        detail.setVersion(1L);
        articleDetailMapper.insert(detail);


        // 3. 保存文章标签
        tags.forEach(s -> {
            ArticleTag tag = new ArticleTag();
            tag.setTagId(s);
            tag.setArticleId(articleId);
            tag.setDeleted(CommonDeletedEnum.NO.getCode());
            articleTagMapper.insert(tag);
        });



        // 发布文章，阅读计数+1
        userFootService.saveOrUpdateUserFoot(DocumentTypeEnum.ARTICLE, articleId, article.getAuthorId(), article.getAuthorId(), OperateTypeEnum.READ);

        // todo 事件发布这里可以进行优化，一次发送多个事件？ 或者借助bit知识点来表示多种事件状态
        // 发布文章创建事件
        SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.CREATE, article));
        // 文章直接上线时，发布上线事件
        SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.ONLINE, article));
        return articleId;
    }

    /**
     * 更新文章
     *
     * @param article
     * @param content
     * @param tags
     * @return
     */
    private Long updateArticle(Article article, String content, Set<Long> tags) {
        // fixme 待补充文章的历史版本支持：若文章处于审核状态，则直接更新上一条记录；否则新插入一条记录
        boolean review = article.getStatus().equals(PushStatusEnum.REVIEW.getCode());
        if (needToReview(article)) {
            article.setStatus(PushStatusEnum.REVIEW.getCode());
        }
        // 更新文章
        article.setUpdateTime(new Date());
        articleMapper.updateById(article);

        // 更新内容
        if (review) {
            articleDetailMapper.updateContent(article.getId(), content);
        } else {
            LambdaQueryWrapper<ArticleDetail> contentQuery = Wrappers.lambdaQuery();
            contentQuery.eq(ArticleDetail::getDeleted, CommonDeletedEnum.NO.getCode())
                    .eq(ArticleDetail::getArticleId, article.getId())
                    .orderByDesc(ArticleDetail::getVersion);
            ArticleDetail latest = articleDetailMapper.selectList(contentQuery).get(0);

            latest.setVersion(latest.getVersion() + 1);
            latest.setId(null);
            latest.setContent(content);
            articleDetailMapper.insert(latest);
        }

        // 标签更新
        if (tags != null && tags.size() > 0) {
            LambdaQueryWrapper<ArticleTag> query = Wrappers.lambdaQuery();
            query.eq(ArticleTag::getArticleId, article.getId())
                    .eq(ArticleTag::getDeleted, CommonDeletedEnum.NO.getCode());
            List<ArticleTag> dbTags = articleTagMapper.selectList(query);

            // 在旧的里面，不在新的里面的标签，设置为删除
            List<Long> toDeleted = new ArrayList<>();
            dbTags.forEach(tag -> {
                if (!tags.contains(tag.getTagId())) {
                    toDeleted.add(tag.getId());
                } else {
                    // 移除已经存在的记录
                    tags.remove(tag.getTagId());
                }
            });
            if (!toDeleted.isEmpty()) {
                articleTagMapper.deleteBatchIds(toDeleted);
            }

            if (!tags.isEmpty()) {
                List<ArticleTag> insertList = new ArrayList<>(tags.size());
                tags.forEach(s -> {
                    ArticleTag tag = new ArticleTag();
                    tag.setTagId(s);
                    tag.setArticleId(article.getId());
                    tag.setDeleted(CommonDeletedEnum.NO.getCode());
                    articleTagMapper.insert(tag);
                });

            }
        }

        // 发布文章待审核事件
        if (article.getStatus() == PushStatusEnum.ONLINE.getCode()) {
            // 修改之后依然直接上线 （对于白名单作者而言）
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.ONLINE, article));
        } else if (review) {
            // 非白名单作者，修改再审核中的文章，依然是待审核状态
            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.REVIEW, article));
        }
        return article.getId();
    }


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
        return article.getStatus() == PushStatusEnum.ONLINE.getCode() && !articleWhiteListService.authorInArticleWhiteList(article.getAuthorId());
    }

    @Override
    public void updateById(Article article){
        articleMapper.updateById(article);
    }
}
