package com.hpl.article.pojo.dto;

import com.hpl.count.pojo.dto.ArticleCountInfoDTO;
import com.hpl.count.pojo.dto.DocumentCntInfoDTO;
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
    private String summary;

    private String categoryId;

    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Integer status;
    private List<TagDTO> tags;

    private DocumentCntInfoDTO countInfo;

}
