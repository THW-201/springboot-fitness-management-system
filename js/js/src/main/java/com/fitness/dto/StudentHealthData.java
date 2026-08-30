package com.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 学生健康数据传输对象（用于AI分析）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentHealthData {
    private Long studentId;
    private Integer age;
    private String gender;
    private BigDecimal height;
    private BigDecimal weight;
    private BigDecimal targetWeight;
    private Integer totalExerciseMinutes;
    private Integer exerciseFrequency;
    private BigDecimal caloriesBurned;
    private String healthPlanDescription;
}
