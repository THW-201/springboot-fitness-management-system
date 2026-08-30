package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预约实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("reservations")
@Schema(description = "预约实体")
public class Reservation {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "预约ID")
    private Long id;

    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;

    @TableField("reservation_type")
    @Schema(description = "预约类型", example = "COURSE")
    private ReservationType reservationType;

    @TableField("course_id")
    @Schema(description = "课程ID")
    private Long courseId;

    @TableField("coach_id")
    @Schema(description = "授课教练ID")
    private Long coachId;

    @TableField("equipment_id")
    @Schema(description = "器材ID")
    private Long equipmentId;

    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @TableField("status")
    @Schema(description = "预约状态", example = "CONFIRMED")
    private ReservationStatus status;

    @TableField("cancel_reason")
    @Schema(description = "取消原因")
    private String cancelReason;

    @TableField("cancelled_at")
    @Schema(description = "取消时间")
    private LocalDateTime cancelledAt;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField(exist = false)
    private User user;
}
