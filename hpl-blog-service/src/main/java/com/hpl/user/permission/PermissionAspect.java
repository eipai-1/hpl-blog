package com.hpl.user.permission;


import com.hpl.exception.ExceptionUtil;
import com.hpl.exception.StatusEnum;
import com.hpl.user.context.ReqInfoContext;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.HandlerMethod;

import java.lang.reflect.Method;

/**
 * @author : rbe
 * @date : 2024/8/14 9:48
 */
@Slf4j
@Component
@Aspect
public class PermissionAspect {

    @Pointcut("@annotation(com.hpl.user.permission.Permission)")
    public void doPermission() {
        log.warn("doPermission");
    }

    @Before("doPermission()")
    public void preHandle(JoinPoint joinPoint) throws Exception {

//        HttpServletRequest request, HttpServletResponse response, Object handler

        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 获取方法签名
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // 获取方法上的注解
        Permission permission = method.getAnnotation(Permission.class);

//        log.warn("method:{}", method);
//        log.warn("permission:{}", permission);
//        log.warn("role:{}", permission.role());

        // 方法没有，则获取类上的注解
        if (permission == null) {
            log.warn("方法上没有");
            permission = method.getDeclaringClass().getAnnotation(Permission.class);
            if (permission == null) {
                log.warn("类上也没有，放行");
                return;
            }
        }

        // 如果权限为游客，则直接放行
        if (permission.role() == UserRole.VISITOR) {
            log.warn("权限为游客，放行");
            return;
        }

        // 如果用户信息为空或用户ID为空，则根据情况处理未登录的访问
        if (ReqInfoContext.getReqInfo() == null || ReqInfoContext.getReqInfo().getUserId() == null) {
            ExceptionUtil.of(StatusEnum.FORBID_NOTLOGIN);
        } else if (permission.role() == UserRole.ADMIN) { // 管理员权限
            // 检查用户是否为超管
            Integer userRole = ReqInfoContext.getReqInfo().getUserInfo().getUserRole();
            // 如果当前用户不是管理员，但方法或类需要管理员权限，则返回无权限错误
            if (!(UserRole.ADMIN.getCode() == userRole.intValue())) {
                // 设置为无权限
                ExceptionUtil.of(StatusEnum.FORBID_ERROR_MIXED,"您不是管理员");
            }
        }

    }
}
