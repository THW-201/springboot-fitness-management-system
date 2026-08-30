package com.fitness.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fitness.entity.SocialReport;

/**
 * 社交举报服务
 */
public interface SocialReportService extends IService<SocialReport> {
    /**
     * 创建举报
     */
    void createReport(Long postId, Long userId, String reason, String description);
    
    /**
     * 处理举报
     */
    void processReport(Long reportId, Long adminId, String status);
}
