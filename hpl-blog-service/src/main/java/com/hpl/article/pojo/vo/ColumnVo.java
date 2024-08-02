package com.hpl.article.pojo.vo;

import com.hpl.article.pojo.dto1.ColumnDTO;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.sidebar.pojo.dto.SideBarDTO;
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

    /**
     * 侧边栏信息
     */
    private List<SideBarDTO> sideBarItems;

}