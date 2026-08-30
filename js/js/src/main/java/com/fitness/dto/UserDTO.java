package com.fitness.dto;

import com.fitness.entity.CoachProfile;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息 DTO（不包含密码）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserDTO {

    @Schema(description = "用户ID", example = "1")
    private Long id;

    @Schema(description = "用户名", example = "zhangsan")
    private String username;

    @Schema(description = "邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "手机号", example = "13800138000")
    private String phone;

    @Schema(description = "真实姓名", example = "张三")
    private String realName;

    @Schema(description = "用户角色", example = "STUDENT", allowableValues = {"ADMIN", "COACH", "STUDENT"})
    private String role;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "状态：1-正常，0-禁用", example = "1")
    private Integer status;

    @Schema(description = "创建时间", example = "2024-01-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "更新时间", example = "2024-01-01T10:00:00")
    private LocalDateTime updatedAt;

    @Schema(description = "学生信息（仅学生角色）")
    private StudentProfileDTO studentProfile;

    @Schema(description = "教练信息（仅教练角色）")
    private CoachProfile coachProfile;

    @Schema(description = "是否已关注（社交功能）")
    private Boolean followed;
}
