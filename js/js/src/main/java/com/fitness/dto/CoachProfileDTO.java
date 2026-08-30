package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 教练信息 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "教练信息")
public class CoachProfileDTO {

    @Schema(description = "教练信息ID", example = "1")
    private Long id;

    @Schema(description = "用户ID", example = "2")
    private Long userId;

    @Schema(description = "专业领域", example = "力量训练")
    private String specialization;

    @Schema(description = "资格证书", example = "国家一级健身教练")
    private String certification;

    @Schema(description = "从业年限", example = "5")
    private Integer experienceYears;

    @Schema(description = "个人简介", example = "拥有5年健身教练经验，擅长力量训练和体能提升")
    private String bio;

    @Schema(description = "头像URL", example = "https://example.com/avatar/coach1.jpg")
    private String avatarUrl;

    @Schema(description = "教练介绍图片URL列表（JSON数组）")
    private String introImages;
}
