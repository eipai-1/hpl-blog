package com.hpl.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hpl.article.pojo.dto.Category1TreeDTO;
import com.hpl.article.pojo.entity.Category1;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/3 9:52
 */
@Mapper
public interface Category1Mapper extends BaseMapper<Category1> {
    List<Category1TreeDTO> selectTreeCategories(String rootId);
}
