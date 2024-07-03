package com.hpl.sitemap.service;

import com.hpl.sitemap.pojo.vo.SiteCntVo;

import java.time.LocalDate;

/**
 * @author : rbe
 * @date : 2024/7/1 8:40
 */
public interface SitemapService {

    /**
     * 查询站点某一天or总的访问信息
     *
     * @param date 日期，为空时，表示查询所有的站点信息
     * @param path 访问路径，为空时表示查站点信息
     * @return
     */
    SiteCntVo querySiteVisitInfo(LocalDate date, String path);
}
