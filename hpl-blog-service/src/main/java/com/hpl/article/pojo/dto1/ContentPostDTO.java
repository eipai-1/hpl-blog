package com.hpl.article.pojo.dto1;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/10 14:03
 */
@Data
public class ContentPostDTO implements Serializable {
    /**
     * 正文内容
     */
    private String content;
}
