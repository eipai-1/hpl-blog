package com.hpl.user.pojo.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/2 10:50
 */
@Data
public class AuthorDTO {

    private static final long serialVersionUID = 1L;

    private Long userId;

    private String nickName;

    private String avatar;

    private String profile;

    private LocalDateTime createTime;


}
