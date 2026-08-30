package com.fitness.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 健康建议传输对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HealthAdviceDTO {
    private String adviceType;
    private String summary;
    private List<String> suggestions;
    private String basedOnData;
}
