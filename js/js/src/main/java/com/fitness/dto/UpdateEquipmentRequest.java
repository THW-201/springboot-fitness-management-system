package com.fitness.dto;

import com.fitness.entity.enums.EquipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 更新器材请求 DTO
 */
@Data
@Schema(description = "更新器材请求")
public class UpdateEquipmentRequest {

    @Schema(description = "器材名称", example = "跑步机Pro")
    @Size(max = 100, message = "器材名称长度不能超过100个字符")
    private String name;

    @Schema(description = "器材类型", example = "有氧器材")
    @Size(max = 50, message = "器材类型长度不能超过50个字符")
    private String equipmentType;

    @Schema(description = "器材描述", example = "升级版专业跑步机，配备更多功能")
    @Size(max = 1000, message = "器材描述长度不能超过1000个字符")
    private String description;

    @Schema(description = "存放位置", example = "健身房A区")
    @Size(max = 100, message = "存放位置长度不能超过100个字符")
    private String location;

    @Schema(description = "器材状态", example = "AVAILABLE", allowableValues = {"AVAILABLE", "IN_USE", "MAINTENANCE", "DAMAGED"})
    private EquipmentStatus status;

    @Schema(description = "购买日期", example = "2023-01-15")
    @PastOrPresent(message = "购买日期不能是未来日期")
    private LocalDate purchaseDate;

    @Schema(description = "最后维护日期", example = "2024-01-10")
    @PastOrPresent(message = "维护日期不能是未来日期")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "器材图片URL", example = "https://example.com/images/treadmill-pro.jpg")
    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    private String imageUrl;

    @Schema(description = "器材介绍图片URL列表（JSON数组）")
    private String introImages;
}
