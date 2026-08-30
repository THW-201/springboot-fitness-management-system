package com.fitness.common.utils;

import com.fitness.common.core.domain.model.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.Assert;

/**
 * 安全服务工具类
 * 
 * @author Fitness System
 */
public class SecurityUtils {
    /**
     * 获取用户信息
     */
    public static LoginUser getLoginUser() {
        return getAuthentication().map(authentication -> {
            Object principal = authentication.getPrincipal();
            if (principal instanceof LoginUser) {
                return (LoginUser) principal;
            }
            return null;
        }).orElse(null);
    }

    /**
     * 获取Authentication
     */
    public static java.util.Optional<Authentication> getAuthentication() {
        return java.util.Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUserId();
        }
        return null;
    }

    /**
     * 获取用户账号
     */
    public static String getUsername() {
        LoginUser loginUser = getLoginUser();
        if (loginUser != null) {
            return loginUser.getUsername();
        }
        return null;
    }

    /**
     * 是否为管理员
     */
    public static boolean isAdmin() {
        Long userId = getUserId();
        return userId != null && userId == 1L;
    }

    /**
     * 验证用户是否认证通过
     */
    public static boolean isAuthenticated() {
        return getAuthentication().map(Authentication::isAuthenticated).orElse(false);
    }

    /**
     * 验证用户是否认证通过，否则抛出异常
     */
    public static void checkAuthenticated() {
        Assert.isTrue(isAuthenticated(), "用户未认证");
    }
}
