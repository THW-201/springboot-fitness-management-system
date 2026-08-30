package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 签退请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "签退请求")
public class CheckOutRequest {

    @Schema(description = "签到状态", example = "CHECKED_OUT")
    private String status;

    @Schema(description = "签退时间")
    private LocalDateTime checkOutTime;

    @Schema(description = "消耗卡路里（可选，系统可自动计算）", example = "300")
    private Integer calories;

    @Schema(description = "消耗卡路里（可选，系统可自动计算）", example = "300.50")
    private BigDecimal caloriesBurned;
}
