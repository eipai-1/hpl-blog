package com.hpl.sidebar.service;

import com.hpl.sidebar.pojo.dto.SideBarDTO;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/7/7 17:41
 */
public interface SidebarService {
    /**
     * 查询首页的侧边栏信息
     *
     * @return
     */
    List<SideBarDTO> queryHomeSidebarList();



    /**
     * 查询文章详情的侧边栏信息
     *
     * @param author    文章作者id
     * @param articleId 文章id
     * @return
     */
    List<SideBarDTO> queryArticleDetailSidebarList(Long author, Long articleId);



    /**
     * 查询教程的侧边栏信息
     *
     * @return
     */
    List<SideBarDTO> queryColumnSidebarList();



}
