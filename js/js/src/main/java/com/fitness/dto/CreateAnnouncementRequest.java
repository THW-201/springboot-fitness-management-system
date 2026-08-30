package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 创建公告请求DTO
 */
@Data
@Schema(description = "创建公告请求DTO")
public class CreateAnnouncementRequest {

    @NotBlank(message = "公告标题不能为空")
    @Schema(description = "公告标题", example = "系统上线通知")
    private String title;

    @NotBlank(message = "公告内容不能为空")
    @Schema(description = "公告内容", example = "大学生健身管理系统正式上线，欢迎使用！")
    private String content;

    @NotNull(message = "公告类型不能为空")
    @Schema(description = "公告类型：SYSTEM-系统公告，ROLE-角色公告，PERSONAL-个人通知", example = "SYSTEM")
    private String type;

    @Schema(description = "目标角色（当type为ROLE时使用）")
    private String[] targetRoles;

    @Schema(description = "目标用户ID（当type为PERSONAL时使用）")
    private Long targetUserId;

    @Schema(description = "公告状态：ACTIVE-活跃，INACTIVE- inactive", example = "ACTIVE")
    private String status;

    @Schema(description = "优先级：LOW-低，MEDIUM-中，HIGH-高", example = "MEDIUM")
    private String priority;

    @Schema(description = "过期时间")
    private LocalDateTime expireAt;
}
