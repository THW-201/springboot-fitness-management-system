package com.fitness.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 预约类型枚举
 */
@Getter
public enum ReservationType {
    COURSE("COURSE", "课程预约"),
    EQUIPMENT("EQUIPMENT", "器材预约");

    @EnumValue
    @JsonValue
    private final String code;
    private final String description;

    ReservationType(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
