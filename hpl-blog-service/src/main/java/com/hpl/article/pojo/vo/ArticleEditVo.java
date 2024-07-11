package com.hpl.article.pojo.vo;

import com.hpl.article.pojo.dto.ArticleDTO;
import com.hpl.article.pojo.dto.CategoryDTO;
import com.hpl.article.pojo.dto.TagDTO;
import lombok.Data;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 14:25
 */
@Data
public class ArticleEditVo {

    private ArticleDTO article;

    private List<CategoryDTO> categories;

    private List<TagDTO> tags;

}

