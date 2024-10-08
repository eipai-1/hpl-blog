package com.hpl.column.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.hpl.column.pojo.dto.ColumnArticleDTO;
import com.hpl.article.pojo.dto1.SearchColumnArticleDTO;
import com.hpl.article.pojo.dto1.SimpleArticleDTO;
import com.hpl.column.pojo.dto.ColumnDirectoryDTO;
import com.hpl.column.pojo.entity.ColumnArticle;
import com.hpl.pojo.CommonPageParam;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/6 11:28
 */
public interface ColumnArticleService extends IService<ColumnArticle> {

    /**
     * 根据专栏id，查询该专栏下的所有文章id
     * @param columnId
     * @return
     */
    List<Long> getArticleIds(Long columnId);

    List<ColumnDirectoryDTO> getDirectoryById(Long columnId);

    /**
     * 根据文章id，查询该文章所属专栏的文章数量
     *
     * @param articleId
     * @return
     */
    Long getCountByArticleId(Long articleId);

    /**
     * 返回专栏最大更新章节数
     *
     * @param columnId
     * @return 专栏内无文章时，返回0；否则返回当前最大的章节数
     */
    int getCountByColumnId(Long columnId);

    /**
     * 根据文章id，查询再所属的专栏信息
     * fixme: 如果一篇文章，在多个专栏内，就会有问题
     *
     * @param articleId
     * @return
     */
    ColumnArticle getByArticleId(Long articleId);


    ColumnArticle getByColumnIdAndSort(Long columnId, Integer sort);

    /**
     * 统计专栏的阅读人数
     *
     * @param columnId
     * @return
     */
    Integer getCountReadUserColumn(Long columnId);

    ColumnArticle getById(Long articleId);

    /**
     * 获取专栏中的第N篇文章
     * @param columnId
     * @param section
     * @return
     */
    ColumnArticle getNthColumnArticle(long columnId, Integer section);


    List<SimpleArticleDTO> listColumnArticles(Long columnId);


    /**
     * 将文章保存到对应的专栏中
     *
     * @param articleId
     * @param columnId
     */
    void saveColumnArticle(Long articleId, Long columnId);

    ColumnArticle getOne(LambdaQueryWrapper<ColumnArticle> wrapper);

    void insert(ColumnArticle columnArticle);

    void deleteById(Long id);

    void update(LambdaUpdateWrapper<ColumnArticle> wrapper);

    List<ColumnArticleDTO> listColumnArticlesDetail(SearchColumnArticleDTO searchColumnArticleDTO, CommonPageParam commonPageParam);

    Integer countColumnArticles(SearchColumnArticleDTO searchColumnArticleDTO);


    /**
     * 删除专栏对应id文章
     * @param articleId
     */
    void deleteArticle(Long articleId);
}
