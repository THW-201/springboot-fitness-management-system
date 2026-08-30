package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 批量删除健康计划请求
 */
@Data
@Schema(description = "批量删除健康计划请求")
public class BatchDeleteHealthPlanRequest {

    @NotEmpty(message = "健康计划ID列表不能为空")
    @Schema(description = "健康计划ID列表", example = "[1, 2, 3]")
    private List<Long> ids;

    @Schema(description = "学生ID", example = "1")
    private Long studentId;
}
