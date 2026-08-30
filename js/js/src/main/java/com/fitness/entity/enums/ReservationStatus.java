package com.fitness.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 预约状态枚举
 */
@Getter
public enum ReservationStatus {
    PENDING("PENDING", "待确认"),
    CONFIRMED("CONFIRMED", "已确认"),
    CANCELLED("CANCELLED", "已取消"),
    COMPLETED("COMPLETED", "已完成");

    @EnumValue
    @JsonValue
    private final String code;
    private final String description;

    ReservationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
