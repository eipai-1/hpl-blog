package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.pojo.dto.CategoryTreeDTO;
import com.hpl.article.pojo.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface CategoryMapper extends BaseMapper<Category> {
    List<CategoryTreeDTO> selectTreeCategories(String rootId);
}
