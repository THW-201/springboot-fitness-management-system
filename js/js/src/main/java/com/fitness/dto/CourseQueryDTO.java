package com.fitness.dto;

import com.fitness.entity.enums.CourseStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 课程查询条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "课程查询条件")
public class CourseQueryDTO {

    @Schema(description = "搜索关键词（课程名称、教练姓名、地点）", example = "瑜伽")
    private String keyword;

    @Schema(description = "课程类型", example = "瑜伽")
    private String courseType;

    @Schema(description = "教练id", example = "1")
    private Long coachId;

    @Schema(description = "课程状态", example = "AVAILABLE")
    private CourseStatus status;
}
