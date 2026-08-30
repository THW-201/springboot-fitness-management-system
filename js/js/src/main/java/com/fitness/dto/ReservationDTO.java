package com.fitness.dto;

import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 预约信息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "预约信息")
public class ReservationDTO {

    @Schema(description = "预约ID", example = "1")
    private Long id;

    @Schema(description = "学生ID", example = "3")
    private Long studentId;

    @Schema(description = "学生姓名", example = "张三")
    private String studentName;

    @Schema(description = "预约类型", example = "COURSE", allowableValues = {"COURSE", "EQUIPMENT"})
    private ReservationType reservationType;

    @Schema(description = "课程ID", example = "1")
    private Long courseId;

    @Schema(description = "课程名称", example = "瑜伽基础课")
    private String courseName;

    @Schema(description = "教练姓名", example = "李四")
    private String coachName;

    @Schema(description = "上课地点", example = "健身房A区")
    private String location;

    @Schema(description = "器材ID", example = "1")
    private Long equipmentId;

    @Schema(description = "器材名称", example = "跑步机")
    private String equipmentName;

    @Schema(description = "开始时间", example = "2024-01-15T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-15T11:30:00")
    private LocalDateTime endTime;

    @Schema(description = "预约状态", example = "CONFIRMED", allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"})
    private ReservationStatus status;

    @Schema(description = "取消原因", example = "临时有事")
    private String cancelReason;

    @Schema(description = "取消时间", example = "2024-01-14T15:30:00")
    private LocalDateTime cancelledAt;

    @Schema(description = "创建时间", example = "2024-01-10T09:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-10T09:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "是否签到")
    private Boolean checkedIn;

    @Schema(description = "是否签退")
    private Boolean checkedOut;

    @Schema(description = "课程时间表")
    private java.util.List<com.fitness.dto.ScheduleItemDTO> courseSchedule;
}
