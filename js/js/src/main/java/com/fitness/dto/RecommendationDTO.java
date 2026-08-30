package com.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * AI推荐结果传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private List<CourseRecommendation> courseRecommendations;
    private List<EquipmentRecommendation> equipmentRecommendations;
    private String reasoning;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CourseRecommendation {
        private Long courseId;
        private String courseName;
        private String reason;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EquipmentRecommendation {
        private Long equipmentId;
        private String equipmentName;
        private String reason;
    }
}
