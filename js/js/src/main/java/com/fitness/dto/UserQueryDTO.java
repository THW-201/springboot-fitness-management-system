package com.fitness.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户查询条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户查询条件")
public class UserQueryDTO {

    @Schema(description = "角色筛选（ADMIN/COACH/STUDENT）", example = "STUDENT")
    private String role;

    @Schema(description = "关键词搜索（用户名/真实姓名/邮箱）", example = "张三")
    private String keyword;
}
