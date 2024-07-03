package com.hpl.global.pojo.vo;

import com.hpl.global.component.GlobalViewConfig;
import com.hpl.sitemap.pojo.vo.SiteCntVo;
import com.hpl.user.pojo.entity.UserInfo;
import lombok.Data;

import java.util.List;

/**
 * @author : rbe
 * @date : 2024/6/30 10:06
 */

@Data
public class GlobalVo {

    /** 网站相关配置 */
    private GlobalViewConfig siteInfo;

    /** 站点统计信息 */
    private SiteCntVo siteStatisticInfo;

    /** 今日的站点统计想你洗 */
    private SiteCntVo todaySiteStatisticInfo;

    /** 环境 */
    private String env;

    /** 是否已登录 */
    private Boolean isLogin;

    /** 登录用户信息 */
    private UserInfo userInfo;

    /** 消息通知数量 */
    private Integer msgNum;

    /** 在线用户人数 */
    private Integer onlineCnt;

    /** 当前域名 */
    private String currentDomain;

    /**
     * Open Graph Protocol (OGP) 标签集合，用于SEO优化。
     * OGP是一组元数据，用于在社交网络上共享网页内容时提供更丰富的信息。
     * 例如，标题、描述和图像等信息可以通过OGP标签进行定义，以改善内容的展示方式。
     */
    private List<SeoTagVo> ogp;

    /**
     * JSON-LD格式的结构化数据，用于搜索引擎优化。
     * JSON-LD是一种在网页中嵌入结构化数据的方式，它使用JSON格式来表示数据。
     * 这种结构化数据可以帮助搜索引擎更好地理解网页的内容，从而有可能在搜索结果中显示更丰富的信息，如评分、图片等。
     */
    private String jsonLd;
}
