package com.hpl.article.service;

import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;

/**
 * @author : rbe
 * @date : 2024/7/10 11:26
 */
public interface ArticleRecommendService {
    /**
     * 文章关联推荐
     *
     * @param articleId
     * @param pageParam
     * @return
     */
    CommonPageListVo<ArticleDTO> relatedRecommend(Long articleId, CommonPageParam pageParam);
}
