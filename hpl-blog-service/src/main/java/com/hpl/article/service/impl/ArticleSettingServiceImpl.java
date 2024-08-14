//package com.hpl.article.service.impl;
//
//import cn.hutool.extra.spring.SpringUtil;
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//
//import com.hpl.article.pojo.dto1.ArticleAdminDTO;
//import com.hpl.article.pojo.dto.ArticlePostDTO;
//import com.hpl.article.pojo.dto1.SearchArticleDTO;
//import com.hpl.article.enent.ArticleMsgEvent;
//import com.hpl.article.pojo.entity.Article;
//import com.hpl.article.pojo.enums.ArticleEventEnum;
//import com.hpl.article.pojo.enums.OperateArticleEnum;
//import com.hpl.article.pojo.enums.PushStatusEnum;
//import com.hpl.article.mapper.ArticleMapper;
//import com.hpl.article.service.ArticleSettingService;
//import com.hpl.exception.StatusEnum;
//import com.hpl.pojo.CommonDeletedEnum;
//import com.hpl.pojo.CommonPageVo;
//import com.hpl.exception.ExceptionUtil;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.stereotype.Service;
//
//
//import java.util.List;
//import java.util.Objects;
//import java.util.function.Consumer;
//import java.util.function.Supplier;
//
///**
// * 文章后台
// *
// * @author louzai
// * @date 2022-09-19
// */
//@Service
//public class ArticleSettingServiceImpl implements ArticleSettingService {
//
//    @Autowired
//    private ArticleMapper articleMapper;
//
//    @Autowired
//    private ColumnArticleService columnArticleService;
//
//    /**
//     * 更新文章
//     *
//     * @param articlePostDTO
//     */
//    @Override
//    @CacheEvict(key = "'sideBar_' + #req.articleId", cacheManager = "caffeineCacheManager", cacheNames = "article")
//    public void updateArticle(ArticlePostDTO articlePostDTO) {
//        if (articlePostDTO.getStatus() != PushStatusEnum.OFFLINE.getCode()
//                && articlePostDTO.getStatus() != PushStatusEnum.ONLINE.getCode()
//                && articlePostDTO.getStatus() != PushStatusEnum.REVIEW.getCode()) {
//            throw ExceptionUtil.of(StatusEnum.ILLEGAL_ARGUMENTS_MIXED, "发布状态不合法!");
//        }
//
//        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Article::getId, articlePostDTO.getArticleId())
//                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode());
//        Article article = articleMapper.selectOne(wrapper);
//
//        if (article == null) {
//            throw ExceptionUtil.of(StatusEnum.RECORDS_NOT_EXISTS, "文章不存在!");
//        }
//
//        if (StringUtils.isNotBlank(articlePostDTO.getTitle())) {
//            article.setTitle(articlePostDTO.getTitle());
//        }
//        if (StringUtils.isNotBlank(articlePostDTO.getShortTitle())) {
//            article.setShortTitle(articlePostDTO.getShortTitle());
//        }
//
//        ArticleEventEnum operateEvent = null;
//        if (articlePostDTO.getStatus() != null) {
//            article.setStatus(articlePostDTO.getStatus());
//            if (articlePostDTO.getStatus() == PushStatusEnum.OFFLINE.getCode()) {
//                operateEvent = ArticleEventEnum.OFFLINE;
//            } else if (articlePostDTO.getStatus() == PushStatusEnum.REVIEW.getCode()) {
//                operateEvent = ArticleEventEnum.REVIEW;
//            } else if (articlePostDTO.getStatus() == PushStatusEnum.ONLINE.getCode()) {
//                operateEvent = ArticleEventEnum.ONLINE;
//            }
//        }
//        articleMapper.updateById(article);
//
//        if (operateEvent != null) {
//            // 发布文章待审核、上线、下线事件
//            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, operateEvent, article));
//        }
//    }
//
//    @Override
//    public CommonPageVo<ArticleAdminDTO> listArticles(SearchArticleDTO searchArticleDTO) {
//
//        // 查询文章列表，分页
//        List<ArticleAdminDTO> articleDTOS = articleMapper.listArticlesByParams(searchArticleDTO);
//
//        // 查询文章总数
//        Long totalCount = articleMapper.countArticleByParams(searchArticleDTO);
//        return CommonPageVo.build(articleDTOS, searchArticleDTO.getPageSize(), searchArticleDTO.getPageNumber(), totalCount);
//    }
//
//    @Override
//    public void deleteArticleById(Long articleId) {
//        Article article = articleMapper.selectById(articleId);
//
//        if (article != null && article.getDeleted() != CommonDeletedEnum.YES.getCode()) {
//            // 查询该文章是否关联了教程，如果已经关联了教程，则不能删除
//            long count = columnArticleService.getCountByArticleId(articleId);
//
//            if (count > 0) {
//                throw ExceptionUtil.of(StatusEnum.ARTICLE_RELATION_TUTORIAL, articleId, "请先解除文章与教程的关联关系");
//            }
//
//            article.setDeleted(CommonDeletedEnum.YES.getCode());
//            articleMapper.updateById(article);
//
//            // 发布文章删除事件
//            SpringUtil.publishEvent(new ArticleMsgEvent<>(this, ArticleEventEnum.DELETE, article));
//        } else {
//            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
//        }
//    }
//
//    @Override
//    public void operateArticle(Long articleId, OperateArticleEnum operate) {
//        LambdaQueryWrapper<Article> wrapper = new LambdaQueryWrapper<>();
//        wrapper.eq(Article::getId, articleId)
//                .eq(Article::getDeleted, CommonDeletedEnum.NO.getCode());
//        Article article = articleMapper.selectOne(wrapper);
//
//        if (article == null) {
//            throw ExceptionUtil.of(StatusEnum.ARTICLE_NOT_EXISTS, articleId);
//        }
//        setArticleState(article, operate);
//        articleMapper.updateById(article);
//    }
//
//    private void setArticleState(Article articleDO, OperateArticleEnum operate) {
//        switch (operate) {
//            case OFFICAL:
//            case CANCEL_OFFICAL:
//                compareAndUpdate(articleDO::getOfficalState, articleDO::setOfficalState, operate.getDbStatCode());
//                return;
//            case TOPPING:
//            case CANCEL_TOPPING:
//                compareAndUpdate(articleDO::getToppingState, articleDO::setToppingState, operate.getDbStatCode());
//                return;
//            case CREAM:
//            case CANCEL_CREAM:
//                compareAndUpdate(articleDO::getCreamState, articleDO::setCreamState, operate.getDbStatCode());
//                return;
//            default:
//        }
//    }
//
//    /**
//     * 相同则直接返回false不用更新；不同则更新,返回true
//     *
//     * @param <T>
//     * @param supplier
//     * @param consumer
//     * @param input
//     */
//    private <T> void compareAndUpdate(Supplier<T> supplier, Consumer<T> consumer, T input) {
//        if (Objects.equals(supplier.get(), input)) {
//            return;
//        }
//        consumer.accept(input);
//    }
//}
