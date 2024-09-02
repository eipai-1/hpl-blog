package com.hpl.column.pojo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author : rbe
 * @date : 2024/8/25 17:06
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ColumnSimpleDTO implements Serializable {

    private Long id;

    private String columnName;
}
