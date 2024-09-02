package com.hpl.user.pojo.dto;

import com.hpl.user.pojo.entity.IpInfo;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/19 9:52
 */
@Data
public class AuthorDetailDTO implements Serializable {

    private Long userId;

    private String nickName;

    private String avatar;

    private String profile;

    private IpInfo ipInfo;

    private LocalDateTime createTime;

    private Long articleCount;

    private Long readCount;

    private Long fansCount;

    private Long praiseCount;

    private Long commentCount;

    private Long collectionCount;




}
