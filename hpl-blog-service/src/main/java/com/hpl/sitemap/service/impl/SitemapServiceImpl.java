package com.hpl.sitemap.service.impl;

import com.hpl.sitemap.pojo.constant.SitemapConstant;
import com.hpl.sitemap.pojo.vo.SiteCntVo;
import com.hpl.sitemap.service.SitemapService;
import com.hpl.util.RedisUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Map;

/**
 * @author : rbe
 * @date : 2024/7/1 8:40
 */
@Service
public class SitemapServiceImpl implements SitemapService {

    /**
     * 根据指定的日期和路径查询网站访问信息。
     *
     * @param date 查询的日期，用于获取指定日期的访问数据。如果为null，则查询所有日期的累计数据。
     * @param path 查询的路径，用于获取指定路径的访问数据。如果为null，则查询整个网站的访问数据。
     * @return SiteCntVo 对象，包含访问总数（pv）和独立访问者数（uv）。
     */
    @Override
    public SiteCntVo querySiteVisitInfo(LocalDate date, String path) {

        // 全局访问信息的键
        String globalKey = SitemapConstant.SITE_VISIT_KEY;
        String day = null, todayKey = globalKey;

        // 如果指定了日期，则构建特定日期的访问信息键，并更新todayKey
        if (date != null) {
            day = SitemapConstant.dateToFormat(date);
            todayKey = globalKey + "_" + day;
        }

        // 访问计数的字段名，分别用于pv（页面访问数）和uv（独立访问者数）
        String pvField = "pv", uvField = "uv";

        // 如果指定了路径，则为字段名添加路径后缀，以区分不同路径的访问数据
        if (path != null) {
            // 表示查询对应路径的访问信息
            pvField += "_" + path;
            uvField += "_" + path;
        }

        // 从Redis中获取指定日期（或全局）和路径的访问数据，包括pv和uv
        Map<String, Integer> map = RedisUtil.hMGet(todayKey, Arrays.asList(pvField, uvField), Integer.class);

        // 初始化返回对象，并填充数据
        SiteCntVo siteInfo = new SiteCntVo();
        siteInfo.setDay(day);
        siteInfo.setPv(map.getOrDefault(pvField, 0));
        siteInfo.setUv(map.getOrDefault(uvField, 0));
        return siteInfo;
    }
}
