package com.hpl.user.context;

import com.hpl.redis.RedisClient;
import com.hpl.user.helper.UserSessionHelper;
import com.hpl.user.service.UserInfoService;
import com.hpl.util.IpUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;


import java.io.IOException;


/**
 * 请求拦截器，每次请求都会拦截，并注入context信息
 *
 * @author : rbe
 * @date : 2024/7/26 10:22
 */
@Component
@Slf4j
@WebFilter(urlPatterns = "/*", filterName = "reqRecordFilter", asyncSupported = true)
public class ReqInfoFilter implements Filter {

    @Resource
    private UserSessionHelper userSessionHelper;

    @Resource
    private UserInfoService userInfoService;


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        log.warn("我来了吗，dofilterlo");
        HttpServletRequest request = null;

        try {

            // 初始化请求信息
            request = this.initReqInfo((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);

            // 继续传递请求到下一个过滤器或目标资源
            filterChain.doFilter(request, servletResponse);
        } finally {

            // 防止val 强引用造成的 内存泄露
            ReqInfoContext.clear();

        }
    }

    private HttpServletRequest initReqInfo(HttpServletRequest request, HttpServletResponse response) {
        if (isStaticURI(request)) {
            // 静态资源直接放行
            return request;
        }

//        StopWatch stopWatch = new StopWatch("请求参数构建");
        try {
            // 手动写入一个session，借助 OnlineUserCountListener 实现在线人数实时统计
//            request.getSession().setAttribute("latestVisit", System.currentTimeMillis());

            ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();

            log.warn("session: {}", request.getHeader("Authorization"));
            String authorizationHeader = request.getHeader("Authorization");
            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                // 去掉Bearer
                String token = authorizationHeader.substring(7);

                Long userId = userSessionHelper.getUserIdBySession(token);

                if (userId != null) {
                    reqInfo.setUserId(userId);

                    reqInfo.setUserInfo(userInfoService.getByUserId(userId));
                }
            }

            reqInfo.setHost(request.getHeader("host"));
            reqInfo.setPath(request.getPathInfo());
            if (reqInfo.getPath() == null) {
                String url = request.getRequestURI();
                int index = url.indexOf("?");
                if (index > 0) {
                    url = url.substring(0, index);
                }
                reqInfo.setPath(url);
            }
            reqInfo.setReferer(request.getHeader("referer"));
            reqInfo.setClientIp(IpUtil.getClientIp(request));
            reqInfo.setUserAgent(request.getHeader("User-Agent"));
//            reqInfo.setDeviceId(getOrInitDeviceId(request, response));

//            request = this.wrapperRequest(request, reqInfo);


            ReqInfoContext.addReqInfo(reqInfo);
            log.warn("这个永远不可能为空,:{}", ReqInfoContext.getReqInfo());

//            // 更新uv/pv计数
//            AsyncUtil.execute(() -> SpringUtil.getBean(SitemapServiceImpl.class).saveVisitInfo(reqInfo.getClientIp(), reqInfo.getPath()));


//            stopWatch.start("回写traceId");
            // 返回头中记录traceId
//            response.setHeader(GLOBAL_TRACE_ID_HEADER, Optional.ofNullable(MdcUtil.getTraceId()).orElse(""));
//            stopWatch.stop();
        } catch (Exception e) {
            log.error("init reqInfo error!", e);
        } finally {

        }

        return request;
    }

//    private javax.servlet.http.HttpServletRequest wrapperRequest(javax.servlet.http.HttpServletRequest request, ReqInfoContext.ReqInfo reqInfo) {
//        if (!HttpMethod.POST.name().equalsIgnoreCase(request.getMethod())) {
//            return request;
//        }
//
//        BodyReaderHttpServletRequestWrapper requestWrapper = new BodyReaderHttpServletRequestWrapper(request);
//        reqInfo.setPayload(requestWrapper.getBodyString());
//        return requestWrapper;
//    }

    private boolean isStaticURI(HttpServletRequest request) {
        return request == null
                || request.getRequestURI().endsWith("css")
                || request.getRequestURI().endsWith("js")
                || request.getRequestURI().endsWith("png")
                || request.getRequestURI().endsWith("ico")
                || request.getRequestURI().endsWith("svg")
                || request.getRequestURI().endsWith("min.js.map")
                || request.getRequestURI().endsWith("min.css.map");
    }

    @Override
    public void destroy() {

    }
}
