package com.hpl.config;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;


import java.io.IOException;

/**
 * @author : rbe
 * @date : 2024/7/24 14:58
 */

@Component
@WebFilter(urlPatterns = "/*", filterName = "CORSFilter")
@Order(1) // 数值越小，优先级越高，因此这个过滤器会先执行
@Slf4j
public class CorsFilterConfig implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 获取请求的源（前端域名）
        String origin = request.getHeader("Origin");
        if (origin == null) {
            origin = request.getHeader("Referer");
        }
        // 允许特定的前端域名进行跨域请求
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:5173"); // 替换为实际前端域名
        // 允许跨域请求携带cookie
        response.setHeader("Access-Control-Allow-Credentials", "true");
        // 允许预检请求的特定HTTP方法
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        // 允许预检请求的特定头信息
        response.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");

        // 对于实际的请求，继续过滤器链
        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
    }
}