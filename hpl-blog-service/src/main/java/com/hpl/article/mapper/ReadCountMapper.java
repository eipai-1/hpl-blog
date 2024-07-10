package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.entity.Article;
import com.hpl.article.entity.ReadCount;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface ReadCountMapper extends BaseMapper<ReadCount> {
}
