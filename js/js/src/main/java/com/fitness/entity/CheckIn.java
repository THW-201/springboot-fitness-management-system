package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 签到实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("check_ins")
@Schema(description = "签到实体")
public class CheckIn {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "签到ID")
    private Long id;

    @TableField("reservation_id")
    @Schema(description = "预约ID")
    private Long reservationId;

    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;

    @TableField("check_in_time")
    @Schema(description = "签到时间")
    private LocalDateTime checkInTime;

    @TableField("check_out_time")
    @Schema(description = "签退时间")
    private LocalDateTime checkOutTime;

    @TableField("duration_minutes")
    @Schema(description = "活动时长(分钟)", example = "60")
    private Integer durationMinutes;

    @TableField("location")
    @Schema(description = "签到位置", example = "健身房A区")
    private String location;

    @TableField("calories_burned")
    @Schema(description = "消耗卡路里", example = "300.50")
    private BigDecimal caloriesBurned;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField("coach_id")
    @Schema(description = "授课教练ID")
    private Long coachId;

}
