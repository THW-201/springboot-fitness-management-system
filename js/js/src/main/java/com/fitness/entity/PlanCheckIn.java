package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康计划打卡实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("plan_check_ins")
@Schema(description = "健康计划打卡实体")
public class PlanCheckIn {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "打卡ID")
    private Long id;

    @TableField("plan_id")
    @Schema(description = "健康计划ID")
    private Long planId;

    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;

    @TableField("check_date")
    @Schema(description = "打卡日期")
    private LocalDate checkDate;

    @TableField("check_in_time")
    @Schema(description = "开始打卡时间")
    private LocalDateTime checkInTime;

    @TableField("check_out_time")
    @Schema(description = "结束打卡时间")
    private LocalDateTime checkOutTime;

    @TableField("duration_minutes")
    @Schema(description = "打卡时长(分钟)")
    private Integer durationMinutes;

    @TableField("status")
    @Schema(description = "打卡状态(IN_PROGRESS, COMPLETED)")
    private String status;

    @TableField("exercise_type")
    @Schema(description = "运动方式")
    private String exerciseType;

    @TableField("calories_burned")
    @Schema(description = "消耗卡路里")
    private Double caloriesBurned;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

}
