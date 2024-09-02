package com.hpl.column.pojo.dto;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author : rbe
 * @date : 2024/8/4 8:56
 */
@Data
@Builder
public class ColumnDirectoryDTO implements Serializable {
    private static final long serialVersionUID = 3646376715620165839L;

    private Long articleId;

    private String title;

    private LocalDateTime updateTime;
}
