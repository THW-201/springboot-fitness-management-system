package com.fitness.entity.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户角色枚举
 */
@Getter
public enum UserRole {
    ADMIN("ADMIN", "管理员"),
    COACH("COACH", "教练"),
    STUDENT("STUDENT", "学生");

    @EnumValue
    @JsonValue
    private final String code;
    private final String description;

    UserRole(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
