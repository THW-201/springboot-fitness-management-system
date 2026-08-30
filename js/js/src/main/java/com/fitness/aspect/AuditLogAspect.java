package com.fitness.aspect;

import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.service.AuditLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 审计日志AOP切面
 * 自动记录关键操作的审计日志
 */
@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditLogAspect {
    
    private final AuditLogService auditLogService;
    
    /**
     * 定义切点：拦截所有Controller的POST、PUT、DELETE方法
     */
    @Pointcut("execution(* com.fitness.controller.*.*(..)) && " +
              "(execution(* *..create*(..)) || " +
              "execution(* *..update*(..)) || " +
              "execution(* *..delete*(..)) || " +
              "execution(* *..reserve*(..)) || " +
              "execution(* *..cancel*(..)))")
    public void auditPointcut() {
    }
    
    /**
     * 在方法成功执行后记录审计日志
     */
    @AfterReturning(pointcut = "auditPointcut()", returning = "result")
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            Long userId = null;
            if (loginUser != null) {
                userId = loginUser.getUserId();
            }
            
            // 获取请求信息
            ServletRequestAttributes attributes = 
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String ipAddress = null;
            String userAgent = null;
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = getClientIpAddress(request);
                userAgent = request.getHeader("User-Agent");
            }
            
            // 解析操作信息
            String methodName = joinPoint.getSignature().getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();
            String action = determineAction(methodName);
            String resourceType = determineResourceType(className);
            
            // 尝试从参数中提取资源ID
            Long resourceId = extractResourceId(joinPoint.getArgs());
            
            // 构建详情信息
            String details = String.format("方法: %s.%s", className, methodName);
            
            // 记录审计日志
            auditLogService.log(userId, action, resourceType, resourceId, 
                              details, ipAddress, userAgent);
                              
        } catch (Exception e) {
            // 审计日志记录失败不应影响主业务流程
            log.error("AOP记录审计日志失败", e);
        }
    }
    
    /**
     * 根据方法名确定操作类型
     */
    private String determineAction(String methodName) {
        if (methodName.startsWith("create") || methodName.startsWith("add") 
            || methodName.startsWith("register") || methodName.startsWith("reserve")) {
            return "CREATE";
        } else if (methodName.startsWith("update") || methodName.startsWith("modify")) {
            return "UPDATE";
        } else if (methodName.startsWith("delete") || methodName.startsWith("remove") 
                   || methodName.startsWith("cancel")) {
            return "DELETE";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 根据类名确定资源类型
     */
    private String determineResourceType(String className) {
        if (className.contains("Course")) {
            return "COURSE";
        } else if (className.contains("Equipment")) {
            return "EQUIPMENT";
        } else if (className.contains("Reservation")) {
            return "RESERVATION";
        } else if (className.contains("User")) {
            return "USER";
        } else if (className.contains("HealthPlan")) {
            return "HEALTH_PLAN";
        } else if (className.contains("CheckIn")) {
            return "CHECK_IN";
        } else {
            return "UNKNOWN";
        }
    }
    
    /**
     * 从方法参数中提取资源ID
     */
    private Long extractResourceId(Object[] args) {
        if (args == null || args.length == 0) {
            return null;
        }
        
        // 尝试从第一个参数提取ID
        for (Object arg : args) {
            if (arg instanceof Long) {
                return (Long) arg;
            }
        }
        
        return null;
    }
    
    /**
     * 获取客户端真实IP地址
     */
    private String getClientIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        
        // 处理多个IP的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        
        return ip;
    }
}
