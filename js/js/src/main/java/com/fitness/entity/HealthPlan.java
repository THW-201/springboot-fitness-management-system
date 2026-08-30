package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fitness.entity.enums.HealthPlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康计划实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_plans")
@Schema(description = "健康计划实体")
public class HealthPlan {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "健康计划ID")
    private Long id;

    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;

    @TableField("coach_id")
    @Schema(description = "授课教练ID")
    private Long coachId;

    @TableField("plan_name")
    @Schema(description = "计划名称", example = "减脂计划")
    private String planName;

    @TableField("description")
    @Schema(description = "计划描述")
    private String description;

    @TableField("target_weight")
    @Schema(description = "目标体重(kg)", example = "60.0")
    private BigDecimal targetWeight;

    @TableField("target_duration_minutes")
    @Schema(description = "目标运动时长(分钟/周)", example = "300")
    private Integer targetDurationMinutes;

    @TableField("current_weight")
    @Schema(description = "当前体重(kg)", example = "65.5")
    private BigDecimal currentWeight;

    @TableField("current_duration_minutes")
    @Schema(description = "当前运动时长(分钟/周)", example = "150")
    private Integer currentDurationMinutes;

    @TableField("start_date")
    @Schema(description = "开始日期")
    private LocalDate startDate;

    @TableField("end_date")
    @Schema(description = "结束日期")
    private LocalDate endDate;

    @TableField("status")
    @Schema(description = "计划状态", example = "ACTIVE")
    private HealthPlanStatus status;

    @TableField("completion_percentage")
    @Schema(description = "完成百分比", example = "50.00")
    private BigDecimal completionPercentage;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
