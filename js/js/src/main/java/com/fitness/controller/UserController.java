package com.fitness.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.StudentProfileDTO;
import com.fitness.dto.UpdateUserRequest;
import com.fitness.dto.UserDTO;
import com.fitness.dto.UserQueryDTO;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理控制器
 * 提供用户信息的增删改查功能
 */
@Tag(name = "用户管理", description = "用户信息管理相关接口")
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 获取用户列表
     * 分页获取用户列表，支持角色筛选和关键词搜索（仅管理员）
     */
    @Operation(summary = "获取用户列表", description = "分页获取用户列表，支持角色筛选和关键词搜索（仅管理员）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "无权访问")
    })
    @GetMapping
    @RequireRole(value = {UserRole.ADMIN})
    public Result<Page<UserDTO>> getUserList(
            Page<UserDTO> page,
            UserQueryDTO query
    ) {
        Page<UserDTO> result = userService.getUserList(page, query);
        return Result.success(result);
    }

    /**
     * 获取用户详情
     * 根据用户ID获取用户详细信息
     */
    @Operation(summary = "获取用户详情", description = "根据用户ID获取用户详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @GetMapping("/{id}")
    public Result<UserDTO> getUserById(
            @Parameter(description = "用户ID", example = "1")
            @PathVariable Long id
    ) {
        UserDTO user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 更新用户信息
     * 更新用户基本信息
     */
    @Operation(summary = "更新用户信息", description = "更新用户基本信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @PutMapping("/{id}")
    public Result<UserDTO> updateUser(
            @Parameter(description = "用户ID", example = "1")
            @PathVariable Long id,
            
            @Valid @RequestBody UpdateUserRequest request
    ) {
        UserDTO user = userService.updateUser(id, request);
        return Result.success(user);
    }

    /**
     * 删除用户
     * 删除指定用户（仅管理员）
     */
    @Operation(summary = "删除用户", description = "删除指定用户（仅管理员）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "用户不存在")
    })
    @DeleteMapping("/{id}")
    @RequireRole(value = {UserRole.ADMIN})
    public Result<Void> deleteUser(
            @Parameter(description = "用户ID", example = "1")
            @PathVariable Long id
    ) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 获取教练负责的学生列表
     * 获取当前教练负责的所有学生（仅教练）
     */
    @Operation(summary = "获取教练负责的学生列表", description = "获取当前教练负责的所有学生（仅教练）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "403", description = "无权访问")
    })
    @GetMapping("/coach/students")
    @RequireRole(value = {UserRole.COACH})
    public Result<Page<UserDTO>> getMyStudents(
            Page<UserDTO> page,
            UserQueryDTO query
    ) {
        // 从登录用户中获取教练ID
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long coachId = loginUser.getUserId();
        Page<UserDTO> students = userService.getStudentsByCoachIdWithPage(page, query, coachId);
        return Result.success(students);
    }

    /**
     * 为学生分配教练
     * 为指定学生分配负责教练（仅管理员）
     */
    @Operation(summary = "为学生分配教练", description = "为指定学生分配负责教练（仅管理员）")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "分配成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "403", description = "无权访问"),
            @ApiResponse(responseCode = "404", description = "学生或教练不存在")
    })
    @PutMapping("/{studentId}/coach/{coachId}")
    @RequireRole(value = {UserRole.ADMIN})
    public Result<Void> assignCoach(
            @Parameter(description = "学生ID", example = "1")
            @PathVariable Long studentId,
            
            @Parameter(description = "教练ID", example = "2")
            @PathVariable Long coachId
    ) {
        userService.assignCoachToStudent(studentId, coachId);
        return Result.success();
    }
    
    /**
     * 获取学生档案
     * 根据用户ID获取学生档案信息
     */
    @Operation(summary = "获取学生档案", description = "根据用户ID获取学生档案信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "404", description = "学生档案不存在")
    })
    @GetMapping("/{userId}/profile")
    public Result<StudentProfileDTO> getStudentProfile(
            @Parameter(description = "用户ID", example = "1")
            @PathVariable Long userId
    ) {
        StudentProfileDTO profile = userService.getStudentProfile(userId);
        return Result.success(profile);
    }
    
    /**
     * 更新学生档案
     * 更新学生档案信息
     */
    @Operation(summary = "更新学生档案", description = "更新学生档案信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "更新成功"),
            @ApiResponse(responseCode = "400", description = "参数错误"),
            @ApiResponse(responseCode = "404", description = "学生档案不存在")
    })
    @PutMapping("/{userId}/profile")
    public Result<StudentProfileDTO> updateStudentProfile(
            @Parameter(description = "用户ID", example = "1")
            @PathVariable Long userId,
            
            @Valid @RequestBody StudentProfileDTO request
    ) {
        StudentProfileDTO profile = userService.updateStudentProfile(userId, request);
        return Result.success(profile);
    }
}
