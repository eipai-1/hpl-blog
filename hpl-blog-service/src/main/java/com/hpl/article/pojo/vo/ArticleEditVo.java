package com.hpl.article.pojo.vo;

import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.pojo.dto1.CategoryDTO;
import com.hpl.article.pojo.dto1.TagDTO;
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

