package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.SocialReport;
import com.fitness.mapper.SocialReportMapper;
import com.fitness.service.SocialReportService;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

/**
 * 社交举报服务实现
 */
@Service
public class SocialReportServiceImpl extends ServiceImpl<SocialReportMapper, SocialReport> implements SocialReportService {
    
    @Override
    public void createReport(Long postId, Long userId, String reason, String description) {
        SocialReport report = new SocialReport();
        report.setPostId(postId);
        report.setUserId(userId);
        report.setReason(reason);
        report.setDescription(description);
        report.setStatus("PENDING");
        save(report);
    }
    
    @Override
    public void processReport(Long reportId, Long adminId, String status) {
        SocialReport report = getById(reportId);
        if (report != null) {
            report.setStatus(status);
            report.setProcessedBy(adminId);
            report.setProcessedAt(LocalDateTime.now());
            updateById(report);
        }
    }
}
