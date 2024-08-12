package com.hpl.article.pojo.vo;

import com.hpl.column.pojo.dto.ColumnDTO;
import com.hpl.pojo.CommonPageListVo;
import lombok.Data;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 18:17
 */
@Data
public class ColumnVo {
    /**
     * 专栏列表
     */
    private CommonPageListVo<ColumnDTO> columns;



}