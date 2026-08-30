package com.fitness.dto;

import com.fitness.entity.enums.EquipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 器材查询条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "器材查询条件")
public class EquipmentQueryDTO {

    @Schema(description = "搜索关键词（器材名称、描述、地点）", example = "跑步机")
    private String keyword;

    @Schema(description = "器材类型", example = "有氧器材")
    private String equipmentType;

    @Schema(description = "器材状态", example = "AVAILABLE")
    private EquipmentStatus status;
}
