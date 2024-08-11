package com.hpl.article.pojo.dto;

import com.hpl.article.pojo.entity.Tag;
import com.hpl.statistic.pojo.dto.ArticleCountInfoDTO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author : rbe
 * @date : 2024/8/7 9:21
 */
@Data
public class MyArticleListDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long articleId;
    private Long authorId;
    private String title;
    private String shortTitle;
    private String summary;

    private String categoryName;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
    private List<TagDTO> tags;
    private ArticleCountInfoDTO countInfo;

}
