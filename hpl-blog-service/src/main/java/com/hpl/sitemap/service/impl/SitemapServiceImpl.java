package com.hpl.sitemap.service.impl;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hpl.article.pojo.dto.SimpleArticleDTO;
import com.hpl.article.enent.ArticleMsgEvent;
import com.hpl.article.pojo.entity.Article;
import com.hpl.article.pojo.enums.ArticleEventEnum;
import com.hpl.article.service.ArticleReadService;
import com.hpl.sitemap.pojo.constant.SitemapConstant;
import com.hpl.sitemap.pojo.vo.SiteCntVo;
import com.hpl.sitemap.pojo.vo.SiteMapVo;
import com.hpl.sitemap.pojo.vo.SiteUrlVo;
import com.hpl.sitemap.service.SitemapService;
import com.hpl.statistic.service.CountService;
import com.hpl.util.DateUtil;
import com.hpl.util.RedisUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : rbe
 * @date : 2024/7/1 8:40
 */
@Service
@Slf4j
public class SitemapServiceImpl implements SitemapService {

    @Value("${view.site.host:https://paicoding.com}")
    private String host;
    private static final int SCAN_SIZE = 100;

    private static final String SITE_MAP_CACHE_KEY = "sitemap";

    @Resource
    private ArticleReadService articleReadService;
    @Resource
    private CountService countService;

    /**
     * 查询站点地图
     * @return 返回站点地图
     */
    @Override
    public SiteMapVo getSiteMap() {
        // key = 文章id, value = 最后更新时间
        Map<String, Long> siteMap = RedisUtil.hGetAll(SITE_MAP_CACHE_KEY, Long.class);
        if (CollectionUtils.isEmpty(siteMap)) {
            // 首次访问时，没有数据，全量初始化
            initSiteMap();
        }
        siteMap = RedisUtil.hGetAll(SITE_MAP_CACHE_KEY, Long.class);
        SiteMapVo vo = initBasicSite();
        if (CollectionUtils.isEmpty(siteMap)) {
            return vo;
        }

        for (Map.Entry<String, Long> entry : siteMap.entrySet()) {
            vo.addUrl(new SiteUrlVo(host + "/article/detail/" + entry.getKey(), DateUtil.time2utc(entry.getValue())));
        }
        return vo;
    }

    /**
     * fixme: 加锁初始化，更推荐的是采用分布式锁
     */
    private synchronized void initSiteMap() {
        long lastId = 0L;
        RedisUtil.del(SITE_MAP_CACHE_KEY);
        while (true) {
            List<SimpleArticleDTO> list = articleReadService.listArticlesOrderById(lastId, SCAN_SIZE);
            // 刷新文章的统计信息
            list.forEach(s -> countService.refreshArticleStatisticInfo(s.getId()));

            // 刷新站点地图信息
            Map<String, Long> map = list.stream().collect(Collectors.toMap(s -> String.valueOf(s.getId()), s -> s.getCreateTime().getTime(), (a, b) -> a));
            RedisUtil.hMSet(SITE_MAP_CACHE_KEY, map);
            if (list.size() < SCAN_SIZE) {
                break;
            }
            lastId = list.get(list.size() - 1).getId();
        }
    }

    private SiteMapVo initBasicSite() {
        SiteMapVo vo = new SiteMapVo();
        String time = DateUtil.time2utc(System.currentTimeMillis());
        vo.addUrl(new SiteUrlVo(host + "/", time));
        vo.addUrl(new SiteUrlVo(host + "/column", time));
        vo.addUrl(new SiteUrlVo(host + "/admin-view", time));
        return vo;
    }

    /**
     * 重新刷新站点地图
     */
    @Override
    public void refreshSitemap() {
        initSiteMap();
    }

    /**
     * 基于文章的上下线，自动更新站点地图
     *
     * @param event
     */
    @EventListener(ArticleMsgEvent.class)
    public void autoUpdateSiteMap(ArticleMsgEvent<Article> event) {
        ArticleEventEnum type = event.getType();
        if (type == ArticleEventEnum.ONLINE) {
            addArticle(event.getContent().getId());
        } else if (type == ArticleEventEnum.OFFLINE || type == ArticleEventEnum.DELETE) {
            rmArticle(event.getContent().getId());
        }
    }

    /**
     * 新增文章并上线
     *
     * @param articleId
     */
    private void addArticle(Long articleId) {
        RedisUtil.hSet(SITE_MAP_CACHE_KEY, String.valueOf(articleId), System.currentTimeMillis());
    }

    /**
     * 删除文章、or文章下线
     *
     * @param articleId
     */
    private void rmArticle(Long articleId) {
        RedisUtil.hDel(SITE_MAP_CACHE_KEY, String.valueOf(articleId));
    }


    /**
     * 采用定时器方案，每天5:15分刷新站点地图，确保数据的一致性
     */
    @Scheduled(cron = "0 15 5 * * ?")
    public void autoRefreshCache() {
        log.info("开始刷新sitemap.xml的url地址，避免出现数据不一致问题!");
        refreshSitemap();
        log.info("刷新完成！");
    }


    /**
     * 保存站点数据模型
     * <p>
     * 站点统计hash：
     * - visit_info:
     * ---- pv: 站点的总pv
     * ---- uv: 站点的总uv
     * ---- pv_path: 站点某个资源的总访问pv
     * ---- uv_path: 站点某个资源的总访问uv
     * - visit_info_ip:
     * ---- pv: 用户访问的站点总次数
     * ---- path_pv: 用户访问的路径总次数
     * - visit_info_20230822每日记录, 一天一条记录
     * ---- pv: 12  # field = 月日_pv, pv的计数
     * ---- uv: 5   # field = 月日_uv, uv的计数
     * ---- pv_path: 2 # 资源的当前访问计数
     * ---- uv_path: # 资源的当天访问uv
     * ---- pv_ip: # 用户当天的访问次数
     * ---- pv_path_ip: # 用户对资源的当天访问次数
     *
     * @param visitIp 访问者ip
     * @param path    访问的资源路径
     */
    @Override
    public void saveVisitInfo(String visitIp, String path) {
        String globalKey = SitemapConstant.SITE_VISIT_KEY;
        String day = SitemapConstant.dateToFormat(LocalDate.now());

        String todayKey = globalKey + "_" + day;

        // 用户的全局访问计数+1
        Long globalUserVisitCnt = RedisUtil.hIncr(globalKey + "_" + visitIp, "pv", 1);
        // 用户的当日访问计数+1
        Long todayUserVisitCnt = RedisUtil.hIncr(todayKey, "pv_" + visitIp, 1);

        RedisUtil.PipelineAction pipelineAction = RedisUtil.pipelineAction();
        if (globalUserVisitCnt == 1) {
            // 站点新用户
            // 今日的uv + 1
            pipelineAction.add(todayKey, "uv"
                    , (connection, key, field) -> {
                        connection.hIncrBy(key, field, 1);
                    });
            pipelineAction.add(todayKey, "uv_" + path
                    , (connection, key, field) -> connection.hIncrBy(key, field, 1));

            // 全局站点的uv
            pipelineAction.add(globalKey, "uv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
            pipelineAction.add(globalKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        } else if (todayUserVisitCnt == 1) {
            // 判断是今天的首次访问，更新今天的uv+1
            pipelineAction.add(todayKey, "uv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
            if (RedisUtil.hIncr(todayKey, "pv_" + path + "_" + visitIp, 1) == 1) {
                // 判断是否为今天首次访问这个资源，若是，则uv+1
                pipelineAction.add(todayKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
            }

            // 判断是否是用户的首次访问这个path，若是，则全局的path uv计数需要+1
            if (RedisUtil.hIncr(globalKey + "_" + visitIp, "pv_" + path, 1) == 1) {
                pipelineAction.add(globalKey, "uv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
            }
        }


        // 更新pv 以及 用户的path访问信息
        // 今天的相关信息 pv
        pipelineAction.add(todayKey, "pv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
        pipelineAction.add(todayKey, "pv_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        if (todayUserVisitCnt > 1) {
            // 非当天首次访问，则pv+1; 因为首次访问时，在前面更新uv时，已经计数+1了
            pipelineAction.add(todayKey, "pv_" + path + "_" + visitIp, (connection, key, field) -> connection.hIncrBy(key, field, 1));
        }


        // 全局的 PV
        pipelineAction.add(globalKey, "pv", (connection, key, field) -> connection.hIncrBy(key, field, 1));
        pipelineAction.add(globalKey, "pv" + "_" + path, (connection, key, field) -> connection.hIncrBy(key, field, 1));

        // 保存访问信息
        pipelineAction.execute();
        if (log.isDebugEnabled()) {
            log.info("用户访问信息更新完成! 当前用户总访问: {}，今日访问: {}", globalUserVisitCnt, todayUserVisitCnt);
        }
    }

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
