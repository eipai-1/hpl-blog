package com.hpl.article.service;


import com.hpl.article.dto.ArticleAdminDTO;
import com.hpl.article.dto.ArticlePostDTO;
import com.hpl.article.dto.SearchArticleDTO;
import com.hpl.article.enums.OperateArticleEnum;
import com.hpl.pojo.CommonPageVo;

/**
 * 文章后台接口
 *
 * @author louzai
 * @date 2022-09-19
 */
public interface ArticleSettingService {

    /**
     * 更新文章
     *
     * @param articlePostDTO
     */
    void updateArticle(ArticlePostDTO articlePostDTO);

    /**
     * 获取文章列表
     *
     * @param searchArticleDTO@return
     */
    CommonPageVo<ArticleAdminDTO> listArticles(SearchArticleDTO searchArticleDTO);

    /**
     * 删除文章
     *
     * @param articleId
     */
    void deleteArticleById(Long articleId);

    /**
     * 操作文章
     *
     * @param articleId
     * @param operate
     */
    void operateArticle(Long articleId, OperateArticleEnum operate);
}
