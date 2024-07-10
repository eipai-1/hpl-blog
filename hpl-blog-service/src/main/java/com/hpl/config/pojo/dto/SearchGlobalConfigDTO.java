package com.hpl.config.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/8 19:06
 */
@Data
public class SearchGlobalConfigDTO {
    // 配置项名称
    private String keywords;
    // 配置项值
    private String value;
    // 备注
    private String comment;
    // 分页
    private Long pageNumber;
    private Long pageSize;
}