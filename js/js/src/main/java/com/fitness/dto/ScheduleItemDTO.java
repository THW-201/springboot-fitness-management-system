package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程上课时间安排项 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程上课时间安排项")
public class ScheduleItemDTO {

    @Schema(description = "星期几", example = "1")
    private String dayOfWeek;

    @Schema(description = "开始时间", example = "10:00")
    private String startTime;

    @Schema(description = "结束时间", example = "11:30")
    private String endTime;
}
