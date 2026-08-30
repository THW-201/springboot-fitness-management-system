package com.fitness.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 课程状态枚举
 */
@Getter
public enum CourseStatus {
    AVAILABLE("AVAILABLE", "可预约"),
    FULL("FULL", "已满员"),
    CANCELLED("CANCELLED", "已取消"),
    RESERVATION("RESERVATION", "已预约"),
    COMPLETED("COMPLETED", "已完成");

    @EnumValue
    @JsonValue
    private final String code;
    private final String description;

    CourseStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
