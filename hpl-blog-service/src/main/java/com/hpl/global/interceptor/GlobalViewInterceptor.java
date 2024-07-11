package com.hpl.global.interceptor;

import cn.hutool.extra.spring.SpringUtil;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.hpl.annotation.permission.Permission;
import com.hpl.annotation.permission.UserRole;
import com.hpl.enums.StatusEnum;
import com.hpl.global.comtext.ReqInfoContext;
import com.hpl.global.service.GlobalInitService;
import com.hpl.pojo.CommonResVo;
import com.hpl.util.JsonUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;


/**
 *  注入全局的配置信息：
 *  - thymleaf 站点信息，基本信息，在这里注入
 *
 * @author : rbe
 * @date : 2024/6/30 14:37
 */
@Slf4j
@Component
public class GlobalViewInterceptor implements AsyncHandlerInterceptor {

    @Autowired
    private GlobalInitService globalInitService;

//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//
//        if(handler instanceof HandlerMethod){
//
//            HandlerMethod handlerMethod = (HandlerMethod) handler;
//
//            Permission permission = handlerMethod.getMethod().getAnnotation(Permission.class);
//
//            if(permission == null){
//                permission = handlerMethod.getBeanType().getAnnotation(Permission.class);
//            }
//
//            if(permission == null || permission.role()== UserRole.ALL){
//                if(ReqInfoContext.getReqInfo()!=null){
//                    SpringUtil.getBean(UserActivityRankService.class).addActivityScore(ReqInfoContext.getReqInfo().getUserId(),
//                            new ActiviryScoreBo().setPath(ReqInfoContext.getReqInfo().getPath()));
//                }
//                return true;
//            }
//
//            // 如果用户信息为空或用户ID为空，则根据情况处理未登录的访问
//            if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
//                // 如果是REST接口，返回未登录的错误信息
//                if (handlerMethod.getMethod().getAnnotation(ResponseBody.class) != null
//                        || handlerMethod.getMethod().getDeclaringClass().getAnnotation(RestController.class) != null) {
//                    // 访问需要登录的rest接口
//                    response.setContentType(MediaType.APPLICATION_JSON_UTF8_VALUE);
//                    response.getWriter().println(JsonUtil.objToStr(CommonResVo.fail(StatusEnum.FORBID_NOTLOGIN)));
//                    response.getWriter().flush();
//                    return false;
//                } else if (request.getRequestURI().startsWith("/api/admin/") || request.getRequestURI().startsWith("/admin/")) {
//                    // 如果是管理员页面，重定向到登录页面
//                    response.sendRedirect("/admin");
//                } else {
//                    // 其他情况，重定向到首页
//                    // 访问需要登录的页面时，直接跳转到登录界面
//                    response.sendRedirect("/");
//                }
//                return false;
//            }
//
//            // 如果当前用户不是管理员，但方法或类需要管理员权限，则返回无权限错误
//            if (permission.role() == UserRole.ADMIN && !UserRole.ADMIN.name().equalsIgnoreCase(ReqInfoContext.getReqInfo().getUserInfo().getUserRole())) {
//                // 设置为无权限
//                response.setStatus(HttpStatus.FORBIDDEN.value());
//                return false;
//            }
//        }
//        // 如果处理器不是方法级别或权限检查通过，则继续处理请求
//        return true;
//    }


    /**
     * 在处理完请求后调用，用于对模型和视图进行额外的处理。
     * 主要用于在模型中添加全局属性，以及在某些情况下初始化登录用户信息。
     *
     * @param request  当前请求的HttpServletRequest对象。
     * @param response 当前响应的HttpServletResponse对象。
     * @param handler  处理请求的对象，可以是控制器或其他类型的处理器。
     * @param modelAndView 视图模型，包含视图名和模型数据。
     * @throws Exception 如果处理过程中发生异常。
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 检查是否有模型和视图需要处理
        if (!ObjectUtils.isEmpty(modelAndView)) {
            // 检查响应状态是否为HTTP OK（200）
            if (response.getStatus() != HttpStatus.OK.value()) {
                try {
                    // 初始化请求信息上下文，用于记录和管理当前请求的相关信息
                    ReqInfoContext.ReqInfo reqInfo = new ReqInfoContext.ReqInfo();
                    // 初始化登录用户信息，确保在异常情况下也能保持登录状态
                    // fixme 对于异常重定向到 /error 时，会导致登录信息丢失，待解决
                    globalInitService.initLoginUser(reqInfo);
                    // 将请求信息添加到上下文中
                    ReqInfoContext.addReqInfo(reqInfo);
                    // 在模型中添加全局属性
                    modelAndView.getModel().put("global", globalInitService.globalAttr());
                    log.warn("global   :{}",globalInitService.globalAttr());
                } finally {
                    // 无论成功与否，清理请求信息上下文
                    ReqInfoContext.clear();
                }
            } else {
                // 如果响应状态为HTTP OK，直接在模型中添加全局属性
                modelAndView.getModel().put("global", globalInitService.globalAttr());
                log.warn("global   :{}",globalInitService.globalAttr());
            }
        }

    }
}
