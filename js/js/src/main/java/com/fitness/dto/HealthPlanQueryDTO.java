package com.fitness.dto;

import com.fitness.entity.enums.HealthPlanStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 健康计划查询条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "健康计划查询条件")
public class HealthPlanQueryDTO {

    @Schema(description = "学生ID（可选，用于指定查询某个学生的健康计划）", example = "1")
    private Long studentId;

    @Schema(description = "搜索关键词（计划名称、学生姓名）", example = "减脂")
    private String keyword;

    @Schema(description = "计划状态", example = "ACTIVE")
    private HealthPlanStatus status;

    @Schema(description = "开始日期（起始）", example = "2024-01-01")
    private LocalDate startDateFrom;

    @Schema(description = "开始日期（结束）", example = "2024-12-31")
    private LocalDate startDateTo;
}
