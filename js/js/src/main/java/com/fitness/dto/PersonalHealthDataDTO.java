package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 个人健康数据统计 DTO
 *
 * 用于返回某个学生的个人健康数据统计，包括：
 * - 运动总时长
 * - 运动次数
 * - 消耗卡路里
 * - 体重变化记录
 * - 课程参与记录
 * - 器材使用记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "个人健康数据统计")
public class PersonalHealthDataDTO {

    @Schema(description = "学生ID")
    private Long studentId;

    @Schema(description = "总运动时长(分钟)")
    private Integer totalExerciseMinutes;

    @Schema(description = "总运动次数")
    private Integer totalExerciseCount;

    @Schema(description = "总消耗卡路里")
    private BigDecimal totalCaloriesBurned;

    @Schema(description = "体重历史记录")
    private List<WeightRecord> weightHistory;

    @Schema(description = "课程参与记录")
    private List<CourseParticipation> courseHistory;

    @Schema(description = "器材使用记录")
    private List<EquipmentUsage> equipmentHistory;

    /**
     * 体重记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "体重记录")
    public static class WeightRecord {

        @Schema(description = "记录日期")
        private String date;

        @Schema(description = "体重(kg)")
        private BigDecimal weight;
    }

    /**
     * 课程参与记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "课程参与记录")
    public static class CourseParticipation {

        @Schema(description = "课程ID")
        private Long courseId;

        @Schema(description = "课程名称")
        private String courseName;

        @Schema(description = "参与日期")
        private String date;

        @Schema(description = "运动时长(分钟)")
        private Integer durationMinutes;
    }

    /**
     * 器材使用记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "器材使用记录")
    public static class EquipmentUsage {

        @Schema(description = "器材ID")
        private Long equipmentId;

        @Schema(description = "器材名称")
        private String equipmentName;

        @Schema(description = "使用日期")
        private String date;

        @Schema(description = "使用时长(分钟)")
        private Integer durationMinutes;
    }
}