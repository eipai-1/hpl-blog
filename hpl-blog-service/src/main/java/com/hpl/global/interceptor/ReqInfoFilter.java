//package com.hpl.global.interceptor;
//
//import cn.hutool.core.date.StopWatch;
//import cn.hutool.extra.spring.SpringUtil;
//import com.hpl.global.context.ReqInfoContext;
//import com.hpl.global.service.GlobalInitService;
//import com.hpl.sitemap.service.impl.SitemapServiceImpl;
//import com.hpl.util.AsyncUtil;
//import com.hpl.util.IpUtil;
//import jakarta.servlet.*;
//import jakarta.servlet.annotation.WebFilter;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//
//
//import java.io.IOException;
//import java.util.Optional;
//
//
///**
// * @author : rbe
// * @date : 2024/7/26 10:22
// */
//@Component
//@Slf4j
//@WebFilter(urlPatterns = "/*", filterName = "reqRecordFilter", asyncSupported = true)
//public class ReqInfoFilter implements Filter {
//
//    @Override
//    public void init(FilterConfig filterConfig) throws ServletException {
//
//    }
//
//    @Override
//    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
//
//        HttpServletRequest request = null;
//
//        try {
//
//            // 初始化请求信息
//            request = this.initReqInfo((HttpServletRequest) servletRequest, (HttpServletResponse) servletResponse);
//
//            // 继续传递请求到下一个过滤器或目标资源
//            filterChain.doFilter(request, servletResponse);
//        } finally {
//
//            ReqInfoContext.clear();
//
//        }
//    }
//
//    private HttpServletRequest initReqInfo(HttpServletRequest request, HttpServletResponse response) {
//
//        StopWatch stopWatch = new StopWatch("请求参数构建");
//        try {
//            // 手动写入一个session，借助 OnlineUserCountListener 实现在线人数实时统计
//            request.getSession().setAttribute("latestVisit", System.currentTimeMillis());
//
//            ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
//            reqInfo.setHost(request.getHeader("host"));
//            reqInfo.setPath(request.getPathInfo());
//            if (reqInfo.getPath() == null) {
//                String url = request.getRequestURI();
//                int index = url.indexOf("?");
//                if (index > 0) {
//                    url = url.substring(0, index);
//                }
//                reqInfo.setPath(url);
//            }
//            reqInfo.setReferer(request.getHeader("referer"));
//            reqInfo.setClientIp(IpUtil.getClientIp(request));
//            reqInfo.setUserAgent(request.getHeader("User-Agent"));
////            reqInfo.setDeviceId(getOrInitDeviceId(request, response));
//
//            request = this.wrapperRequest(request, reqInfo);
//
//            // 初始化登录信息
//            SpringUtil.getBean(GlobalInitService.class).initLoginUser(reqInfo)
//
//            ReqInfoContext.addReqInfo(reqInfo);
//
//            // 更新uv/pv计数
//            AsyncUtil.execute(() -> SpringUtil.getBean(SitemapServiceImpl.class).saveVisitInfo(reqInfo.getClientIp(), reqInfo.getPath()));
//
//
////            stopWatch.start("回写traceId");
//            // 返回头中记录traceId
////            response.setHeader(GLOBAL_TRACE_ID_HEADER, Optional.ofNullable(MdcUtil.getTraceId()).orElse(""));
////            stopWatch.stop();
//        } catch (Exception e) {
//            log.error("init reqInfo error!", e);
//        } finally {
//
//        }
//
//        return request;
//    }
//
//    @Override
//    public void destroy() {
//
//    }
//}
