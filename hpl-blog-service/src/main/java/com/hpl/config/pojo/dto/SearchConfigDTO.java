package com.hpl.config.pojo.dto;

import lombok.Data;

/**
 * @author : rbe
 * @date : 2024/7/9 10:04
 */
@Data
public class SearchConfigDTO {
    /**
     * 类型
     */
    private Integer type;

    /**
     * 名称
     */
    private String name;

    /**
     * 分页
     */
    private Long pageNumber;
    private Long pageSize;

}
