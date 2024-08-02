package com.hpl.controller.home.vo;

import com.hpl.article.pojo.dto1.ArticleDTO;
import com.hpl.article.pojo.dto1.CategoryDTO;
import com.hpl.controller.home.dto.CarouseDTO;
import com.hpl.pojo.CommonPageListVo;
import com.hpl.sidebar.pojo.dto.SideBarDTO;
import com.hpl.user.pojo.entity.UserInfo;
import lombok.Data;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/10 18:51
 */
@Data
public class IndexVo {
    /**
     * 分类列表
     */
    private List<CategoryDTO> categories;

    /**
     * 当前选中的分类
     */
    private String currentCategory;

    /**
     * 当前选中的类目id
     */
    private Long categoryId;

    /**
     * top 文章列表
     */
    private List<ArticleDTO> topArticles;

    /**
     * 文章列表
     */
    private CommonPageListVo<ArticleDTO> articles;

    /**
     * 登录用户信息
     */
    private UserInfo user;

    /**
     * 侧边栏信息
     */
    private  List<SideBarDTO> sideBarItems;

    /**
     * 轮播图
     */
    private List<CarouseDTO> homeCarouselList;
}