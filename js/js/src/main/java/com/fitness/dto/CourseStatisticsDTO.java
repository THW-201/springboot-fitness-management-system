package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 课程统计数据 DTO
 *
 * 用于返回课程的统计信息，包括：
 * - 课程预约人数
 * - 实际签到人数
 * - 签到率
 * - 课程容量
 * - 使用率
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程统计数据")
public class CourseStatisticsDTO {

    @Schema(description = "课程ID")
    private Long courseId;

    @Schema(description = "课程名称")
    private String courseName;

    @Schema(description = "总预约人数")
    private Integer totalReservations;

    @Schema(description = "取消人数")
    private Integer cancelledReservations;

    @Schema(description = "实际人数")
    private Integer actualReservations;

    @Schema(description = "总签到人数")
    private Integer totalCheckIns;

    @Schema(description = "签到率(签到人数 / 预约人数)")
    private BigDecimal checkInRate;

    @Schema(description = "课程容量(最大可预约人数)")
    private Integer capacity;

    @Schema(description = "课程使用率(预约人数 / 容量)")
    private BigDecimal utilizationRate;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}