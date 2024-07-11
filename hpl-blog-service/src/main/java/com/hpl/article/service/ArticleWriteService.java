package com.hpl.article.service;


import com.hpl.article.pojo.dto.ArticlePostDTO;
import com.hpl.article.pojo.entity.Article;

public interface ArticleWriteService {

    /**
     * 保存or更新文章
     *
     * @param articlePostDTO 上传的文章体
     * @param author 作者
     * @return 返回文章主键
     */
    Long saveArticle(ArticlePostDTO articlePostDTO , Long author);

    /**
     * 删除文章
     *
     * @param articleId   文章id
     * @param loginUserId 执行操作的用户
     */
    void deleteArticle(Long articleId, Long loginUserId);

    void updateById(Article article);
}
