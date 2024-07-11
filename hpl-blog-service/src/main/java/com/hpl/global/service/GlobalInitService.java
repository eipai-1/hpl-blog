package com.hpl.global.service;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.hpl.global.comtext.ReqInfoContext;
import com.hpl.global.component.GlobalViewConfig;
import com.hpl.global.pojo.entity.Seo;
import com.hpl.global.pojo.vo.GlobalVo;
import com.hpl.notify.service.NotifyMagService;
import com.hpl.sitemap.service.SitemapService;
import com.hpl.statistic.service.StatisticUserService;
import com.hpl.user.pojo.entity.UserInfo;
import com.hpl.user.service.UserInfoService;
import com.hpl.user.service.UserService;
import com.hpl.util.NumUtil;
import com.hpl.util.SessionUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDate;
import java.util.Optional;

/**
 * @author : rbe
 * @date : 2024/6/30 11:20
 */
@Slf4j
@Service
public class GlobalInitService {

    @Value("${env.name}")
    private String env;

    @Autowired
    private UserInfoService userInfoService;

    @Resource
    private GlobalViewConfig globalViewConfig;

    @Resource
    private NotifyMagService notifyMagService;

    @Resource
    private SeoInjectService seoInjectService;

    @Resource
    private StatisticUserService statisticUserService;

    @Resource
    private SitemapService sitemapService;

    /**
     * 此函数生成一个包含全局属性的GlobalVo对象。
     * 它根据环境、站点信息、在线用户数、访问统计信息等填充对象。
     * 如果SEO信息可用，它会从请求上下文中获取，否则使用默认的SEO设置。
     * 同时检查用户是否登录，并提供相应状态和用户信息。
     * 根据请求的URL确定当前领域（column, chat 或 article）。
     *
     * @return GlobalVo 对象，包含了环境信息、站点信息、在线用户数、访问统计信息、
     * SEO元数据、用户登录状态及消息数量等。
     */
    public GlobalVo globalAttr() {
        GlobalVo vo = new GlobalVo();

        // 设置环境信息
        vo.setEnv(env);
        // 设置站点信息
        vo.setSiteInfo(globalViewConfig);
        // 设置在线用户数
        vo.setOnlineCnt(statisticUserService.getOnlineUserCnt());
        // 设置站点访问统计信息
        vo.setSiteStatisticInfo(sitemapService.querySiteVisitInfo(null, null));
        // 设置当天站点访问统计信息
        vo.setTodaySiteStatisticInfo(sitemapService.querySiteVisitInfo(LocalDate.now(), null));

        // 设置SEO元数据，如果请求上下文中的SEO信息为空则使用默认SEO
        if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getSeo() == null
                || CollectionUtils.isEmpty(ReqInfoContext.getReqInfo().getSeo().getOgp())) {
            Seo seo = seoInjectService.defaultSeo();
            vo.setOgp(seo.getOgp());
            vo.setJsonLd(JSONUtil.toJsonStr(seo.getJsonLd()));
        } else {
            Seo seo = ReqInfoContext.getReqInfo().getSeo();
            vo.setOgp(seo.getOgp());
            vo.setJsonLd(JSONUtil.toJsonStr(seo.getJsonLd()));
        }

        // 检查用户登录状态并设置相关信息
        try {
            if (ReqInfoContext.getReqInfo() != null && NumUtil.gtZero(ReqInfoContext.getReqInfo().getUserId())) {
                vo.setIsLogin(true);
                vo.setUserInfo(ReqInfoContext.getReqInfo().getUserInfo());
                vo.setMsgNum(ReqInfoContext.getReqInfo().getMsgNum());
            } else {
                vo.setIsLogin(false);
            }

            // 根据请求URI设置当前领域
            HttpServletRequest request = (HttpServletRequest) ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
            if (request.getRequestURI().startsWith("/column")) {
                vo.setCurrentDomain("column");
            } else if (request.getRequestURI().startsWith("/chat")) {
                vo.setCurrentDomain("chat");
            } else {
                vo.setCurrentDomain("article");
            }
        } catch (Exception e) {
            log.error("loginCheckError：", e);
        }

        return vo;
    }


    /**
     * 初始化登录用户信息。此方法检查请求中的Cookie来尝试初始化用户登录状态。
     *
     * 此方法首先从当前请求中获取 {@link HttpServletRequest} 对象，
     * 然后检查请求的Cookies是否为空。如果Cookies为空，方法直接返回。
     * 接着，尝试从Cookies中找到名为 {@link UserService#SESSION_KEY} 的Cookie。
     * 如果找到了这个Cookie，其值将被传递给 {@link #initLoginUser(String, ReqInfoContext.ReqInfo)} 方法
     * 以进一步处理登录用户的初始化，同时传入原始的请求信息。
     */
    public void initLoginUser(ReqInfoContext.ReqInfo reqInfo) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes())
                .getRequest();

        // 如果请求的Cookies为空，方法直接返回
        if (request.getCookies() == null) {
            return;
        }

        // 从请求中获取指定名称的Cookie，如果存在则调用initLoginUser方法进行处理
        Optional.ofNullable(SessionUtil.getCookieByName(request, UserService.SESSION_KEY))
                .ifPresent(cookie -> initLoginUser(cookie.getValue(), reqInfo));
    }

    /**
     * 初始化登录用户的信息。
     * 通过会话ID获取用户信息，并将用户相关的信息设置到请求信息上下文中。
     * 如果用户信息存在，则还会设置用户ID、用户详细信息以及用户的消息数量。
     *
     * @param session 用户的会话ID，用于标识和跟踪用户会话。
     * @param reqInfo 请求信息上下文，用于存储和传递与请求相关的各种信息。
     */
    public void initLoginUser(String session, ReqInfoContext.ReqInfo reqInfo){
        // 根据会话ID和特定的参数（此处为null）获取用户信息。
        UserInfo userInfo = userInfoService.getUserInfoBySessionId(session,null);

        // 设置当前请求的会话ID。
        reqInfo.setSession(session);

        // 如果用户信息存在。
        if(userInfo != null) {
            // 设置用户ID到请求信息上下文中。
            reqInfo.setUserId(userInfo.getUserId());
            // 设置用户详细信息到请求信息上下文中。
            reqInfo.setUserInfo(userInfo);
            // 查询并设置用户的消息数量到请求信息上下文中。
            reqInfo.setMsgNum(notifyMagService.queryUserNotifyMsgCount(userInfo.getUserId()));
        }
    }

}
