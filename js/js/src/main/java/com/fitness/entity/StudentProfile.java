package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fitness.entity.enums.Gender;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学生信息实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("student_profiles")
@Schema(description = "学生信息实体")
public class StudentProfile {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "学生信息ID")
    private Long id;

    @TableField("user_id")
    @Schema(description = "用户ID")
    private Long userId;

    @TableField("student_number")
    @Schema(description = "学号", example = "2024001")
    private String studentNumber;

    @TableField("coach_id")
    @Schema(description = "负责教练ID")
    private Long coachId;

    @TableField("gender")
    @Schema(description = "性别", example = "MALE")
    private Gender gender;

    @TableField("age")
    @Schema(description = "年龄", example = "20")
    private Integer age;

    @TableField("height")
    @Schema(description = "身高(cm)", example = "175.5")
    private BigDecimal height;

    @TableField("weight")
    @Schema(description = "体重(kg)", example = "65.5")
    private BigDecimal weight;

    @TableField("avatar_url")
    @Schema(description = "头像URL")
    private String avatarUrl;

    @TableField("late_cancellation_count")
    @Schema(description = "迟到取消次数", example = "0")
    private Integer lateCancellationCount;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
