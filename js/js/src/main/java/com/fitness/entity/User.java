package com.fitness.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fitness.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("users")
@Schema(description = "用户实体")
public class User {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "用户ID")
    private Long id;

    @TableField("username")
    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @TableField("password")
    @Schema(description = "加密密码")
    private String password;

    @TableField("email")
    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @TableField("phone")
    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @TableField("real_name")
    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @TableField("role")
    @Schema(description = "角色", example = "STUDENT")
    private UserRole role;

    @TableField("avatar_url")
    @Schema(description = "头像URL")
    private String avatarUrl;

    @TableField("status")
    @Schema(description = "状态：1-正常，0-禁用", example = "1")
    private Integer status;

    @TableField(value = "created_at", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @TableField(value = "updated_at", fill = FieldFill.INSERT_UPDATE)
    @Schema(description = "更新时间")
    private LocalDateTime updatedAt;
}
