package com.hpl.column.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.column.pojo.dto.ColumnArticleDTO;
import com.hpl.article.pojo.dto1.SimpleArticleDTO;
import com.hpl.column.pojo.dto.ColumnDirectoryDTO;
import com.hpl.column.pojo.entity.ColumnArticle;
import com.hpl.pojo.CommonPageParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface ColumnArticleMapper extends BaseMapper<ColumnArticle> {

    /**
     * 查询专栏下所有的文章id
     * @param columnId
     * @return
     */
    List<Long> getArticleIds(Long columnId);

    /**
     * 根据教程 ID 查询当前教程中最大的 section
     *
     * @param columnId
     * @return 教程内无文章时，返回0
     */
    @Select("select ifnull(max(section), 0) from column_article where column_id = #{columnId}")
    int getCountByColumnId(@Param("columnId") Long columnId);


    /**
     * 统计专栏的阅读人数
     *
     * @param columnId
     * @return
     */
    Long countColumnReadUserNums(@Param("columnId") Long columnId);


    /**
     * 查询文章
     *
     * @param columnId
     * @param section
     * @return
     */
    ColumnArticle getColumnArticle(@Param("columnId") Long columnId, @Param("section") Integer section);

    /**
     * 查询文章列表
     *
     * @param columnId
     * @return
     */
    List<SimpleArticleDTO> listColumnArticles(@Param("columnId") Long columnId);

    /**
     * 根据教程 ID 文章名称查询文章列表
     *
     * @param columnId
     * @param articleTitle
     * @return
     */
    List<ColumnArticleDTO> listColumnArticlesByColumnIdArticleName(@Param("columnId") Long columnId,
                                                                   @Param("articleTitle") String articleTitle,
                                                                   @Param("pageParam") CommonPageParam pageParam);

    Long countColumnArticlesByColumnIdArticleName(Long columnId, String articleTitle);


}
