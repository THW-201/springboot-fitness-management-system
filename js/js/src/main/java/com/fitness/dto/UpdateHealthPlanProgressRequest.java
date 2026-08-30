package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 更新健康计划进度请求
 */
@Data
@Schema(description = "更新健康计划进度请求")
public class UpdateHealthPlanProgressRequest {

    @Schema(description = "健康计划ID", example = "1")
    private Long id;

    @Schema(description = "当前体重", example = "65.5")
    private BigDecimal currentWeight;

    @Schema(description = "当前运动时长（分钟）", example = "120")
    private Integer currentDurationMinutes;
}
