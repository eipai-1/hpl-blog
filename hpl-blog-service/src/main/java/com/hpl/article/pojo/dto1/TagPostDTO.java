package com.hpl.article.pojo.dto1;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/6 18:31
 */
@Data
public class TagPostDTO implements Serializable {

    /** ID */
    private Long tagId;

    /** 标签名称 */
    private String tag;

    /** 类目ID */
    private Long categoryId;
}