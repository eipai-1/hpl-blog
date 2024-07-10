package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.entity.Article;
import com.hpl.article.entity.Category;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
}
