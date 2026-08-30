package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建课程请求 DTO
 */
@Data
@Schema(description = "创建课程请求")
public class CreateCourseRequest {

    @Schema(description = "课程名称", example = "瑜伽基础课", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 100, message = "课程名称长度不能超过100个字符")
    private String name;

    @Schema(description = "课程描述", example = "适合初学者的瑜伽课程，帮助学员掌握基本体式和呼吸技巧")
    @Size(max = 1000, message = "课程描述长度不能超过1000个字符")
    private String description;

    @Schema(description = "授课教练ID", example = "2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "授课教练ID不能为空")
    @Positive(message = "授课教练ID必须为正数")
    private Long coachId;

    @Schema(description = "课程类型", example = "瑜伽", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "课程类型不能为空")
    @Size(max = 50, message = "课程类型长度不能超过50个字符")
    private String courseType;

    @Schema(description = "容量", example = "30", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "容量不能为空")
    @Min(value = 1, message = "容量至少为1")
    @Max(value = 200, message = "容量不能超过200")
    private Integer capacity;

    @Schema(description = "开始时间", example = "2024-01-15T10:00:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "开始时间不能为空")
    @Future(message = "开始时间必须是未来时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2024-01-15T11:30:00", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "结束时间不能为空")
    @Future(message = "结束时间必须是未来时间")
    private LocalDateTime endTime;

    @Schema(description = "上课地点", example = "健身房A区", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "上课地点不能为空")
    @Size(max = 100, message = "上课地点长度不能超过100个字符")
    private String location;

    @Schema(description = "课程图片URL")
    private String imageUrl;

    @Schema(description = "课程上课时间安排")
    private List<ScheduleItemDTO> classSchedule;
}
