package com.hpl.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.article.pojo.dto.*;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.vo.ArticleListVo;
import com.hpl.column.pojo.dto.ColumnDirectoryDTO;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.pojo.CommonPageParam;

import java.util.List;

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

    /**
     * 返回 优质作者信息
     */
    List<TopAuthorDTO> getTopFourAuthor(Long categoryId);

    /**
     * 获取文章排行
     * @return
     */
    List<TopArticleDTO> getTopEight();


    /**
     * 根据文章id获取作者id
     * @param articleId
     * @return
     */
    Long getAuthorIdById(Long articleId);

    /**
     * 获取文章的id，短标题和更新时间，形成目录
     * @param articleId
     * @return
     */
    ColumnDirectoryDTO getDirectoryById(Long articleId);

    /**
     * 保存文章，当articleId存在时，表示更新记录； 不存在时，表示插入
     *
     * @param articlePostDTO
     * @param authorId
     * @return
     */
    Long saveOrUpdate(ArticlePostDTO articlePostDTO, Long authorId);


    /**
     * 删除文章
     *
     * @param articleId   文章id
     * @param loginUserId 执行操作的用户
     */
    void deleteArticle(Long articleId, Long loginUserId);


    List<MyArticleListDTO> listMyArticles(SearchMyArticleDTO searchMyArticleDTO, Long userId);
}
