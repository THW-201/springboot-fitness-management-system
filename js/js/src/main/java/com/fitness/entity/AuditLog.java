package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 审计日志实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("audit_logs")
@Schema(description = "审计日志实体")
public class AuditLog {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "审计日志ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "操作用户ID")
    private Long userId;

    @TableField("action")
    @Schema(description = "操作类型", example = "CREATE")
    private String action;

    @TableField("resource_type")
    @Schema(description = "资源类型", example = "COURSE")
    private String resourceType;

    @TableField("resource_id")
    @Schema(description = "资源ID")
    private Long resourceId;

    @TableField("details")
    @Schema(description = "操作详情")
    private String details;

    @TableField("ip_address")
    @Schema(description = "IP地址", example = "192.168.1.1")
    private String ipAddress;

    @TableField("user_agent")
    @Schema(description = "用户代理")
    private String userAgent;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
