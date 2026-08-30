package com.fitness.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 器材状态枚举
 */
@Getter
public enum EquipmentStatus {
    AVAILABLE("AVAILABLE", "可用"),
    IN_USE("IN_USE", "使用中"),
    MAINTENANCE("MAINTENANCE", "维护中"),
    RESERVATION("RESERVATION", "已预约"),
    DAMAGED("DAMAGED", "已损坏");

    @EnumValue
    @JsonValue
    private final String code;
    private final String description;

    EquipmentStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
