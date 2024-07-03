package com.hpl.sitemap.pojo.constant;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 站点相关地图
 *
 * @author : rbe
 * @date : 2024/7/2 14:20
 */
public class SitemapConstant {
    public static final String SITE_VISIT_KEY = "visit_info";

    public static String dateToFormat(LocalDate date) {
        return DateTimeFormatter.ofPattern("yyyyMMdd").format(date);
    }
}

