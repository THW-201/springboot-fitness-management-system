package com.fitness.service;

/**
 * 审计日志服务接口
 */
public interface AuditLogService {
    
    /**
     * 记录操作日志
     * @param userId 用户ID
     * @param action 操作类型
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @param details 操作详情
     * @param ipAddress IP地址
     * @param userAgent 用户代理
     */
    void log(Long userId, String action, String resourceType, Long resourceId, 
             String details, String ipAddress, String userAgent);
}
