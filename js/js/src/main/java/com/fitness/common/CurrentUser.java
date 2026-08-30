package com.fitness.common;

import java.lang.annotation.*;

/**
 * 当前用户注解
 * 用于从请求上下文中获取当前登录用户的ID
 */
@Target({ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CurrentUser {
}
