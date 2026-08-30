package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 教练信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("coach_profiles")
@Schema(description = "教练信息实体")
public class CoachProfile {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "教练信息ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("specialization")
    @Schema(description = "专业领域", example = "力量训练")
    private String specialization;

    @TableField("certification")
    @Schema(description = "资格证书", example = "国家一级健身教练")
    private String certification;

    @TableField("experience_years")
    @Schema(description = "从业年限", example = "5")
    private Integer experienceYears;

    @TableField("bio")
    @Schema(description = "个人简介")
    private String bio;

    @TableField("avatar_url")
    @Schema(description = "头像URL")
    private String avatarUrl;

    @TableField("intro_images")
    @Schema(description = "教练介绍图片URL列表（JSON数组）")
    private String introImages;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
