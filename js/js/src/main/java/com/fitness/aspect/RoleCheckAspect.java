package com.fitness.aspect;

import com.fitness.annotation.RequireRole;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.common.ResultCode;
import com.fitness.entity.enums.UserRole;
import com.fitness.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 角色权限检查切面
 * 拦截带有@RequireRole注解的方法，验证用户角色
 */
@Slf4j
@Aspect
@Component
public class RoleCheckAspect {

    /**
     * 在方法执行前检查用户角色
     */
    @Before("@annotation(com.fitness.annotation.RequireRole) || @within(com.fitness.annotation.RequireRole)")
    public void checkRole(JoinPoint joinPoint) {
        // 获取方法签名
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        
        // 获取注解（优先从方法获取，如果方法没有则从类获取）
        RequireRole requireRole = method.getAnnotation(RequireRole.class);
        if (requireRole == null) {
            requireRole = joinPoint.getTarget().getClass().getAnnotation(RequireRole.class);
        }
        
        if (requireRole == null) {
            return;
        }
        
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        
        if (loginUser == null || loginUser.getRoles() == null) {
            log.warn("Unauthenticated user attempted to access protected resource: {}", method.getName());
            throw new BusinessException(ResultCode.UNAUTHORIZED, "未登录或登录已过期");
        }
        
        // 获取用户角色（包含 ROLE_ 前缀）
        Set<String> userRoles = loginUser.getRoles().stream()
                .collect(Collectors.toSet());
        
        // 获取允许的角色（添加 ROLE_ 前缀）
        Set<String> allowedRoles = Arrays.stream(requireRole.value())
                .map(role -> "ROLE_" + role.name())
                .collect(Collectors.toSet());
        
        // 检查用户是否拥有允许的角色
        boolean hasPermission = userRoles.stream()
                .anyMatch(allowedRoles::contains);
        
        if (!hasPermission) {
            log.warn("User with roles {} attempted to access resource requiring roles {}: {}", 
                    userRoles, allowedRoles, method.getName());
            throw new BusinessException(ResultCode.FORBIDDEN, requireRole.message());
        }
        
        log.debug("User with roles {} successfully accessed resource: {}", userRoles, method.getName());
    }
}