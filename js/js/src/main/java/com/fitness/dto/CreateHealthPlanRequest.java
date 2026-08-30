package com.fitness.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 创建健康计划请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "创建健康计划请求")
public class CreateHealthPlanRequest {

    @NotBlank(message = "计划名称不能为空")
    @Schema(description = "计划名称", example = "减脂计划", required = true)
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

    @NotNull(message = "开始日期不能为空")
    @Schema(description = "开始日期", example = "2024-01-01", required = true)
    private LocalDate startDate;

    @NotNull(message = "结束日期不能为空")
    @Schema(description = "结束日期", example = "2024-03-31", required = true)
    private LocalDate endDate;

    @Schema(description = "学生ID")
    private Long studentId;
}
