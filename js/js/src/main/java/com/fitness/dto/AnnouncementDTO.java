package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公告DTO
 */
@Data
@Schema(description = "公告DTO")
public class AnnouncementDTO {

    @Schema(description = "公告ID")
    private Long id;

    @Schema(description = "公告标题", example = "系统上线通知")
    private String title;

    @Schema(description = "公告内容", example = "大学生健身管理系统正式上线，欢迎使用！")
    private String content;

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

    @Schema(description = "查看次数", example = "0")
    private Integer viewCount;

    @Schema(description = "创建人ID")
    private Long createdBy;

    @Schema(description = "创建人姓名")
    private String createdByName;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
