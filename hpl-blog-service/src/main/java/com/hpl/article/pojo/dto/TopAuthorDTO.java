package com.hpl.article.pojo.vo;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/7/28 11:35
 */
@Data
public class TopAuthorVO implements Serializable {

    private Long authorId;

    private String authorName;

    private String authorAvatar;

    private String authorProfile;

    private Long articleCount;

    private Long fansCount;

    private Boolean isFollow;

    private LocalDateTime createTime;

//    private Long readCount;
//
//    private Long praiseCount;
}
