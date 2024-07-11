package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.pojo.dto.ArticleAdminDTO;
import com.hpl.article.pojo.dto.SearchArticleDTO;
import com.hpl.article.pojo.dto.SimpleArticleDTO;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.entity.ReadCount;
import com.hpl.pojo.CommonPageParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {



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

}
