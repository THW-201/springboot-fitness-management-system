package com.fitness.dto;

import com.fitness.entity.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程信息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程信息")
public class CourseDTO {

    @Schema(description = "课程ID", example = "1")
    private Long id;

    @Schema(description = "课程名称", example = "瑜伽基础课")
    private String name;

    @Schema(description = "课程描述", example = "适合初学者的瑜伽课程")
    private String description;

    @Schema(description = "授课教练ID", example = "2")
    private Long coachId;

    @Schema(description = "授课教练姓名", example = "李教练")
    private String coachName;

    @Schema(description = "课程类型", example = "瑜伽")
    private String courseType;

    @Schema(description = "容量", example = "30")
    private Integer capacity;

    @Schema(description = "当前报名人数", example = "15")
    private Integer currentEnrollment;

    @Schema(description = "剩余名额", example = "15")
    private Integer availableSlots;

    @Schema(description = "开始时间", example = "2024-01-15T10:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-15T11:30:00")
    private LocalDateTime endTime;

    @Schema(description = "上课地点", example = "健身房A区")
    private String location;

    @Schema(description = "课程状态", example = "AVAILABLE", allowableValues = {"AVAILABLE", "FULL", "CANCELLED", "COMPLETED"})
    private CourseStatus status;

    @Schema(description = "课程图片URL", example = "https://example.com/images/yoga.jpg")
    private String imageUrl;

    @Schema(description = "创建者ID", example = "1")
    private Long createdBy;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "课程上课时间安排")
    private List<ScheduleItemDTO> classSchedule;
    
    @Schema(description = "总预约数")
    private Integer totalReservations;
    
    @Schema(description = "取消预约数")
    private Integer cancelledReservations;
} 
