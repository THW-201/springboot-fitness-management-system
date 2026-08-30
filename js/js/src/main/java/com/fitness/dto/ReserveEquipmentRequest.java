package com.fitness.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 预约器材请求 DTO
 */
@Data
@Schema(description = "预约器材请求")
public class ReserveEquipmentRequest {

    @Schema(description = "器材ID", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "器材ID不能为空")
    @Positive(message = "器材ID必须为正数")
    private Long equipmentId;

    @Schema(description = "开始时间", example = "2024-01-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-15T11:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束时间不能为空")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private LocalDateTime endTime;
}
