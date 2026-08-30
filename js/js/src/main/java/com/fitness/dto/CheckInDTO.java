package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 签到信息DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "签到信息")
public class CheckInDTO {

    @Schema(description = "签到ID", example = "1")
    private Long id;

    @Schema(description = "预约ID", example = "1")
    private Long reservationId;

    @Schema(description = "学生ID", example = "1")
    private Long studentId;

    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "课程名称", example = "瑜伽课")
    private String courseName;

    @Schema(description = "签到时间", example = "2024-01-15T09:00:00")
    private LocalDateTime checkInTime;

    @Schema(description = "签退时间", example = "2024-01-15T10:30:00")
    private LocalDateTime checkOutTime;

    @Schema(description = "活动时长(分钟)", example = "90")
    private Integer durationMinutes;

    @Schema(description = "签到位置", example = "健身房A区")
    private String location;

    @Schema(description = "消耗卡路里", example = "300.50")
    private BigDecimal caloriesBurned;

    @Schema(description = "创建时间", example = "2024-01-15T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-15T10:30:00")
    private LocalDateTime updatedAt;

    @Schema(description = "预约类型", example = "COURSE")
    private String reservationType;

    @Schema(description = "状态", example = "CHECKED_IN")
    private String status;

    @Schema(description = "时长(分钟)")
    private Integer duration;

    @Schema(description = "消耗卡路里")
    private Integer calories;
}
