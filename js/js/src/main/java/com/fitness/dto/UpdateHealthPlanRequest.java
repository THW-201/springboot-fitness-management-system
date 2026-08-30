package com.fitness.dto;

import com.fitness.entity.enums.HealthPlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 更新健康计划请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "更新健康计划请求")
public class UpdateHealthPlanRequest {

    @Schema(description = "健康计划ID")
    private Long id;

    @Schema(description = "计划名称", example = "减脂计划")
    private String planName;

    @Schema(description = "计划描述", example = "通过有氧运动和力量训练，在3个月内减重5kg")
    private String description;

    @Positive(message = "目标体重必须大于0")
    @Schema(description = "目标体重(kg)", example = "60.0")
    private BigDecimal targetWeight;

    @Positive(message = "目标运动时长必须大于0")
    @Schema(description = "目标运动时长(分钟/周)", example = "300")
    private Integer targetDurationMinutes;

    @Positive(message = "当前体重必须大于0")
    @Schema(description = "当前体重(kg)", example = "65.5")
    private BigDecimal currentWeight;

    @Schema(description = "开始日期", example = "2024-01-01")
    private LocalDate startDate;

    @Schema(description = "结束日期", example = "2024-03-31")
    private LocalDate endDate;

    @Schema(description = "计划状态", example = "ACTIVE", allowableValues = {"ACTIVE", "COMPLETED", "ABANDONED"})
    private HealthPlanStatus status;

    @NotNull(message = "学生ID不能为空")
    @Schema(description = "学生ID")
    private Long studentId;
}
