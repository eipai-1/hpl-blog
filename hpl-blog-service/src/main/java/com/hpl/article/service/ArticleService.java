package com.hpl.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto.ArticleDTO;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.vo.ArticleListVo;
import com.hpl.article.pojo.vo.TopAuthorVO;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;

/**
 * @author : rbe
 * @date : 2024/7/28 9:00
 */
public interface ArticleService extends IService<Article> {

    /**
     * 查询某个分类下的文章，支持翻页
     *
     * @param categoryId
     * @param pageParam
     * @return
     */
    CommonPageListVo<ArticleListVo> listArticlesByCategory(Long categoryId, CommonPageParam pageParam);

//    TopAuthorVO getTopAuthor();
}
