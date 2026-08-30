package com.fitness.dto;

import com.fitness.entity.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 更新课程请求 DTO
 */
@Data
@Schema(description = "更新课程请求")
public class UpdateCourseRequest {

    @Schema(description = "课程名称", example = "瑜伽进阶课")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    private String name;

    @Schema(description = "课程描述", example = "适合有一定基础的学员，深入学习高级体式")
    @Size(max = 1000, message = "课程描述长度不能超过1000个字符")
    private String description;

    @Schema(description = "授课教练ID", example = "3")
    @Positive(message = "授课教练ID必须为正数")
    private Long coachId;

    @Schema(description = "课程类型", example = "瑜伽")
    @Size(max = 50, message = "课程类型长度不能超过50个字符")
    private String courseType;

    @Schema(description = "容量", example = "25")
    @Min(value = 1, message = "容量至少为1")
    @Max(value = 200, message = "容量不能超过200")
    private Integer capacity;

    @Schema(description = "开始时间", example = "2024-01-15T14:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-15T15:30:00")
    private LocalDateTime endTime;

    @Schema(description = "上课地点", example = "健身房B区")
    @Size(max = 100, message = "上课地点长度不能超过100个字符")
    private String location;

    @Schema(description = "课程状态", example = "AVAILABLE", allowableValues = {"AVAILABLE", "FULL", "CANCELLED", "COMPLETED"})
    private CourseStatus status;

    @Schema(description = "课程图片URL")
    private String imageUrl;

    @Schema(description = "课程上课时间安排")
    private List<ScheduleItemDTO> classSchedule;
}
