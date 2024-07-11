package com.hpl.article.pojo.vo;

import com.hpl.article.pojo.dto.ArticleDTO;
import com.hpl.pojo.CommonPageListVo;
import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/10 14:21
 */
@Data
public class ArticleListVo {
    /**
     * 归档类型
     */
    private String archives;
    /**
     * 归档id
     */
    private Long archiveId;

    private CommonPageListVo<ArticleDTO> articles;
}