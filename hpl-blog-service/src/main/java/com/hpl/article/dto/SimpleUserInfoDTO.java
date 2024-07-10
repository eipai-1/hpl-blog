package com.hpl.article.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * 用户基本信息
 *
 * @author : rbe
 * @date : 2024/7/5 9:13
 */
@Data
@Accessors(chain = true)
public class SimpleUserInfoDTO implements Serializable {
    private static final long serialVersionUID = 4802653694786272120L;

//    @ApiModelProperty("作者ID")
    private Long userId;

//    @ApiModelProperty("作者名")
    private String name;

//    @ApiModelProperty("作者头像")
    private String avatar;

//    @ApiModelProperty("作者简介")
    private String profile;
}
