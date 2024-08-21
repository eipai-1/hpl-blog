package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.pojo.dto.SimpleAuthorCountDTO;
import com.hpl.article.pojo.dto1.ArticleAdminDTO;
import com.hpl.article.pojo.dto1.SearchArticleDTO;
import com.hpl.article.pojo.dto1.SimpleArticleDTO;
import com.hpl.article.pojo.entity.Article;
import com.hpl.pojo.CommonPageParam;
import com.hpl.statistic.pojo.entity.ReadCount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    /**
     * 获取当前分类下前四位作者的id和文章数
     * @param categoryId
     */
    List<SimpleAuthorCountDTO> getTopFourAuthor(String categoryId);


    /**
     * 根据类目 + 标签查询文章
     *
     * @param category
     * @param tagIds
     * @param pageParam
     * @return
     */
    List<ReadCount> listArticleByCategoryAndTags(@Param("categoryId") Long category,
                                                 @Param("tags") List<Long> tagIds,
                                                 @Param("pageParam") CommonPageParam pageParam);

    /**
     * 根据阅读次数获取热门文章
     *
     * @param pageParam
     * @return
     */
    List<SimpleArticleDTO> listArticlesByReadCounts(CommonPageParam pageParam);

    List<ArticleAdminDTO> listArticlesByParams(SearchArticleDTO searchArticleDTO);

    Long countArticleByParams(SearchArticleDTO searchArticleDTO);



    /**
     * 通过id遍历文章, 用于生成sitemap.xml
     *
     * @param lastId
     * @param size
     * @return
     */
    List<SimpleArticleDTO> listArticlesOrderById(@Param("lastId") Long lastId, @Param("size") int size);


    Set<Long> getArticleIdsByAuthorId(Long authorId);

}
