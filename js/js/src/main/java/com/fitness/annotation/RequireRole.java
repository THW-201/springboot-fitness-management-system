package com.fitness.annotation;

import com.fitness.entity.enums.UserRole;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 角色权限注解
 * 用于标注需要特定角色才能访问的方法
 *
 * 使用示例:
 * @RequireRole(UserRole.ADMIN)
 * public void deleteUser(Long id) { ... }
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequireRole {

    /**
     * 允许访问的角色列表
     * 用户只需拥有其中一个角色即可访问
     */
    UserRole[] value();

    /**
     * 错误提示信息
     */
    String message() default "无权访问该资源";
}