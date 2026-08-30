package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

/**
 * 创建器材请求 DTO
 */
@Data
@Schema(description = "创建器材请求")
public class CreateEquipmentRequest {

    @Schema(description = "器材名称", example = "跑步机", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "器材名称不能为空")
    @Size(max = 100, message = "器材名称长度不能超过100个字符")
    private String name;

    @Schema(description = "器材类型", example = "有氧器材", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "器材类型不能为空")
    @Size(max = 50, message = "器材类型长度不能超过50个字符")
    private String equipmentType;

    @Schema(description = "器材描述", example = "专业跑步机，适合有氧训练，配备心率监测功能")
    @Size(max = 1000, message = "器材描述长度不能超过1000个字符")
    private String description;

    @Schema(description = "存放位置", example = "健身房B区", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "存放位置不能为空")
    @Size(max = 100, message = "存放位置长度不能超过100个字符")
    private String location;

    @Schema(description = "购买日期", example = "2023-01-15")
    @PastOrPresent(message = "购买日期不能是未来日期")
    private LocalDate purchaseDate;

    @Schema(description = "最后维护日期", example = "2024-01-10")
    @PastOrPresent(message = "维护日期不能是未来日期")
    private LocalDate lastMaintenanceDate;

    @Schema(description = "器材图片URL", example = "https://example.com/images/treadmill.jpg")
    @Size(max = 255, message = "图片URL长度不能超过255个字符")
    @Pattern(regexp = "^(https?://)?[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*$|^$", message = "图片URL格式不正确")
    private String imageUrl;

    @Schema(description = "器材介绍图片URL列表（JSON数组）")
    private String introImages;
}
