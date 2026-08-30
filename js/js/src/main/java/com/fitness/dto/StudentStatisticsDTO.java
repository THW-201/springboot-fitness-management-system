package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 学生统计数据 DTO
 *
 * 用于返回学生的运动统计数据，包括：
 * - 预约次数
 * - 签到次数
 * - 总运动时长
 * - 总消耗卡路里
 * - 活跃度评分
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "学生统计数据")
public class StudentStatisticsDTO {

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "学生姓名")
    private String studentName;

    @Schema(description = "预约总次数")
    private Integer totalReservations;

    @Schema(description = "签到总次数")
    private Integer totalCheckIns;

    @Schema(description = "总运动时长(分钟)")
    private Integer totalExerciseMinutes;

    @Schema(description = "总消耗卡路里")
    private BigDecimal totalCaloriesBurned;

    @Schema(description = "活跃度评分(根据签到次数和运动时长计算)")
    private Integer activityScore;

}