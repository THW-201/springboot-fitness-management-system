package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.fitness.entity.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 课程实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("courses")
@Schema(description = "课程实体")
public class Course {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "课程ID")
    private Long id;

    @TableField("name")
    @Schema(description = "课程名称", example = "瑜伽基础课")
    private String name;

    @TableField("description")
    @Schema(description = "课程描述")
    private String description;

    @TableField("coach_id")
    @Schema(description = "授课教练ID")
    private Long coachId;

    @TableField("course_type")
    @Schema(description = "课程类型", example = "瑜伽")
    private String courseType;

    @TableField("capacity")
    @Schema(description = "容量", example = "30")
    private Integer capacity;

    @TableField("current_enrollment")
    @Schema(description = "当前报名人数", example = "15")
    private Integer currentEnrollment;

    @TableField("start_time")
    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @TableField("end_time")
    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @TableField("location")
    @Schema(description = "上课地点", example = "健身房A区")
    private String location;

    @TableField("image_url")
    @Schema(description = "课程图片URL")
    private String imageUrl;

    @TableField("status")
    @Schema(description = "课程状态", example = "AVAILABLE")
    private CourseStatus status;

    @TableField("created_by")
    @Schema(description = "创建者ID")
    private Long createdBy;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField(value = "class_schedule", typeHandler = JacksonTypeHandler.class)
    @Schema(description = "课程上课时间安排")
    private List<ScheduleItem> classSchedule;

    /**
     * 课程上课时间安排项
     */
    @Data
    public static class ScheduleItem {
        private String dayOfWeek;
        private String startTime;
        private String endTime;
    }
}
