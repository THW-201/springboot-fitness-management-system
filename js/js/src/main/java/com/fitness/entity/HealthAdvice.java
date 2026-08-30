package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康建议实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_advice")
@Schema(description = "健康建议实体")
public class HealthAdvice {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "健康建议ID")
    private Long id;

    @TableField("student_id")
    @Schema(description = "学生ID")
    private Long studentId;

    @TableField("advice_type")
    @Schema(description = "建议类型", example = "运动建议")
    private String adviceType;

    @TableField("content")
    @Schema(description = "建议内容")
    private String content;

    @TableField("based_on_data")
    @Schema(description = "基于的数据(JSON格式)")
    private String basedOnData;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;
}
