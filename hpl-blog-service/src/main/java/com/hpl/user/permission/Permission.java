package com.hpl.user.permission;

import java.lang.annotation.*;

/**
 * @author : rbe
 * @date : 2024/6/30 14:42
 */
@Target({ElementType.METHOD,ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Permission {

    /**
     * 限定权限
     *
     * @return
     */
    UserRole role() default UserRole.VISITOR;
}
