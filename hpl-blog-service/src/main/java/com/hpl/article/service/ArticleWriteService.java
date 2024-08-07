package com.hpl.article.service;


import com.hpl.article.pojo.dto.ArticlePostDTO;
import com.hpl.article.pojo.entity.Article;

public interface ArticleWriteService {


    /**
     * 删除文章
     *
     * @param articleId   文章id
     * @param loginUserId 执行操作的用户
     */
    void deleteArticle(Long articleId, Long loginUserId);

    void updateById(Article article);
}
