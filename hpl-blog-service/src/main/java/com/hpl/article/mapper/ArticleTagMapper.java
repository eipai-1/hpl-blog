package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.pojo.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    @Select("select id from article_tag where deleted = 1")
    List<Long> getDeletedTagIds();
}
