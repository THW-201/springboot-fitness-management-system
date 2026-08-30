package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 器材统计数据 DTO
 *
 * 用于返回健身器材的使用统计信息，包括：
 * - 预约次数
 * - 使用时长
 * - 平均使用时长
 * - 使用频率
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "器材统计数据")
public class EquipmentStatisticsDTO {

    @Schema(description = "器材ID")
    private Long equipmentId;

    @Schema(description = "器材名称")
    private String equipmentName;

    @Schema(description = "总预约次数")
    private Integer totalReservations;

    @Schema(description = "总使用时长(分钟)")
    private Integer totalUsageMinutes;

    @Schema(description = "平均使用时长(分钟)")
    private BigDecimal averageUsageMinutes;

    @Schema(description = "使用频率(次数)")
    private Integer usageFrequency;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}