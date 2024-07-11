package com.hpl.article.pojo.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/7/3 15:21
 */
@Data
public class TagDTO implements Serializable {
    private static final long serialVersionUID = -8614833588325787479L;

    /** 标签id */
    private Long tagId;

    /** 标签名称 */
    private String tag;

    /** 标签状态 */
    private Integer status;

    private Boolean selected;
}
