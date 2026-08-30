package com.fitness.dto;

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
 * 健康计划信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康计划信息")
public class HealthPlanDTO {

    @Schema(description = "健康计划ID", example = "1")
    private Long id;

    @Schema(description = "学生ID", example = "1")
    private Long studentId;

    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "计划名称", example = "减脂计划")
    private String planName;

    @Schema(description = "计划描述", example = "通过有氧运动和力量训练，在3个月内减重5kg")
    private String description;

    @Schema(description = "目标体重(kg)", example = "60.0")
    private BigDecimal targetWeight;

    @Schema(description = "目标运动时长(分钟/周)", example = "300")
    private Integer targetDurationMinutes;

    @Schema(description = "当前体重(kg)", example = "65.5")
    private BigDecimal currentWeight;

    @Schema(description = "当前运动时长(分钟/周)", example = "150")
    private Integer currentDurationMinutes;

    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-03-31")
    private LocalDate endDate;

    @Schema(description = "计划状态", example = "ACTIVE", allowableValues = {"ACTIVE", "COMPLETED", "ABANDONED"})
    private HealthPlanStatus status;

    @Schema(description = "完成百分比", example = "50.00")
    private BigDecimal completionPercentage;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-15T10:00:00")
    private LocalDateTime updatedAt;
}
