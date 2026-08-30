package com.fitness.dto;

import com.fitness.entity.enums.EquipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 器材信息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "器材信息")
public class EquipmentDTO {

    @Schema(description = "器材ID", example = "1")
    private Long id;

    @Schema(description = "器材名称", example = "跑步机")
    private String name;

    @Schema(description = "器材类型", example = "有氧器材")
    private String equipmentType;

    @Schema(description = "器材描述", example = "专业跑步机，适合有氧训练")
    private String description;

    @Schema(description = "存放位置", example = "健身房B区")
    private String location;

    @Schema(description = "器材状态", example = "AVAILABLE", allowableValues = {"AVAILABLE", "IN_USE", "MAINTENANCE", "DAMAGED"})
    private EquipmentStatus status;

    @Schema(description = "购买日期", example = "2023-01-15")
    private LocalDate purchaseDate;

    @Schema(description = "最后维护日期", example = "2024-01-10")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "器材图片URL", example = "https://example.com/images/treadmill.jpg")
    private String imageUrl;

    @Schema(description = "器材介绍图片URL列表（JSON数组）")
    private String introImages;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "负责教练ID")
    private Long coachId;
}
