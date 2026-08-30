package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 签到请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "签到请求")
public class CheckInRequest {

    @NotNull(message = "预约ID不能为空")
    @Schema(description = "预约ID", example = "1", required = true)
    private Long reservationId;

    @Schema(description = "签到位置", example = "健身房A区")
    private String location;
}
