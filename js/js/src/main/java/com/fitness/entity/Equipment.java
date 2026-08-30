package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fitness.entity.enums.EquipmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 器材实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("equipment")
@Schema(description = "器材实体")
public class Equipment {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "器材ID")
    private Long id;

    @TableField("name")
    @Schema(description = "器材名称", example = "跑步机")
    private String name;

    @TableField("equipment_type")
    @Schema(description = "器材类型", example = "有氧器材")
    private String equipmentType;

    @TableField("description")
    @Schema(description = "器材描述")
    private String description;

    @TableField("location")
    @Schema(description = "存放位置", example = "健身房B区")
    private String location;

    @TableField("status")
    @Schema(description = "器材状态", example = "AVAILABLE")
    private EquipmentStatus status;

    @TableField("purchase_date")
    @Schema(description = "购买日期")
    private LocalDate purchaseDate;

    @TableField("last_maintenance_date")
    @Schema(description = "最后维护日期")
    private LocalDate lastMaintenanceDate;

    @TableField("image_url")
    @Schema(description = "器材图片URL")
    private String imageUrl;

    @TableField("intro_images")
    @Schema(description = "器材介绍图片URL列表（JSON数组）")
    private String introImages;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;

    @TableField("coach_id")
    @Schema(description = "负责教练ID")
    private Long coachId;
}
