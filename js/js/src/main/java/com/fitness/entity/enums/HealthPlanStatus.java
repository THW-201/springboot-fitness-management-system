package com.fitness.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 健康计划状态枚举
 */
@Getter
public enum HealthPlanStatus {
    ACTIVE("ACTIVE", "进行中"),
    COMPLETED("COMPLETED", "已完成"),
    ABANDONED("ABANDONED", "已放弃");

    @EnumValue
    @JsonValue
    private final String code;
    private final String description;

    HealthPlanStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
