package com.fitness.service.impl;

import com.fitness.entity.AuditLog;
import com.fitness.mapper.AuditLogMapper;
import com.fitness.service.AuditLogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 审计日志服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditLogServiceImpl implements AuditLogService {
    
    private final AuditLogMapper auditLogMapper;
    
    @Override
    public void log(Long userId, String action, String resourceType, Long resourceId, 
                    String details, String ipAddress, String userAgent) {
        try {
            AuditLog auditLog = new AuditLog();
            auditLog.setUserId(userId);
            auditLog.setAction(action);
            auditLog.setResourceType(resourceType);
            auditLog.setResourceId(resourceId);
            auditLog.setDetails(details);
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setCreatedAt(LocalDateTime.now());
            
            auditLogMapper.insert(auditLog);
            
            log.debug("审计日志记录成功 - 用户: {}, 操作: {}, 资源: {}/{}", 
                userId, action, resourceType, resourceId);
                
        } catch (Exception e) {
            // 审计日志记录失败不应影响主业务流程
            log.error("审计日志记录失败", e);
        }
    }
}
