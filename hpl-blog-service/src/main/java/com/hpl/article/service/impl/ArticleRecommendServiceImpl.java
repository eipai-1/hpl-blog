package com.hpl.article.service.impl;

import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.entity.ArticleTag;
import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.service.ArticleReadService;
import com.hpl.article.service.ArticleRecommendService;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;
import com.hpl.sidebar.service.SidebarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/10 11:27
 */
@Service
public class ArticleRecommendServiceImpl implements ArticleRecommendService {

    @Autowired
    private ArticleReadService articleReadService;




    @Autowired
    private SidebarService sidebarService;

    /**
     * 查询文章关联推荐列表
     *
     * @param articleId
     * @param page
     * @return
     */
    @Override
    public CommonPageListVo<ArticleDTO> relatedRecommend(Long articleId, CommonPageParam page) {
        Article article = articleReadService.getById(articleId);
        if (article == null) {
            return CommonPageListVo.emptyVo();
        }
        List<Long> tagIds = articleReadService.listTagsByArticleId(articleId).stream()
                .map(ArticleTag::getTagId).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(tagIds)) {
            return CommonPageListVo.emptyVo();
        }

        List<Article> recommendArticles = articleReadService.listRelatedArticlesOrderByReadCount(article.getCategoryId(), tagIds, page);
        if (recommendArticles.removeIf(s -> s.getId().equals(articleId))) {
            // 移除推荐列表中的当前文章
            page.setPageSize(page.getPageSize() - 1);
        }
        return articleReadService.buildArticleListVo(recommendArticles, page.getPageSize());
    }


}