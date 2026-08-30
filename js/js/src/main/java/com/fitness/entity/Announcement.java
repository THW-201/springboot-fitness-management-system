package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 公告实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("announcements")
@Schema(description = "公告实体")
public class Announcement {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "公告ID")
    private Long id;

    @TableField("title")
    @Schema(description = "公告标题", example = "系统上线通知")
    private String title;

    @TableField("content")
    @Schema(description = "公告内容", example = "大学生健身管理系统正式上线，欢迎使用！")
    private String content;

    @TableField("type")
    @Schema(description = "公告类型：SYSTEM-系统公告，ROLE-角色公告，PERSONAL-个人通知", example = "SYSTEM")
    private String type;

    @TableField("target_roles")
    @Schema(description = "目标角色（当type为ROLE时使用）")
    private String targetRoles;

    @TableField("target_user_id")
    @Schema(description = "目标用户ID（当type为PERSONAL时使用）")
    private Long targetUserId;

    @TableField("status")
    @Schema(description = "公告状态：ACTIVE-活跃，INACTIVE- inactive", example = "ACTIVE")
    private String status;

    @TableField("priority")
    @Schema(description = "优先级：LOW-低，MEDIUM-中，HIGH-高", example = "MEDIUM")
    private String priority;

    @TableField("expire_at")
    @Schema(description = "过期时间")
    private LocalDateTime expireAt;

    @TableField("view_count")
    @Schema(description = "查看次数", example = "0")
    private Integer viewCount;

    @TableField("created_by")
    @Schema(description = "创建人ID")
    private Long createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
