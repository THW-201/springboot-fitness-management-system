package com.fitness.controller;

import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.CreateHealthPlanRequest;
import com.fitness.dto.HealthPlanDTO;
import com.fitness.dto.UpdateHealthPlanRequest;
import com.fitness.entity.HealthPlan;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.HealthPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 健康计划控制器
 * 提供健康计划的创建、更新、删除、查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/health-plans")
@RequiredArgsConstructor
@Tag(name = "健康计划", description = "健康计划相关接口，包括创建、更新、删除、查询健康计划等")
public class HealthPlanController {

    private final HealthPlanService healthPlanService;

    /**
     * 获取健康计划列表
     * 所有用户都可以查看
     */
    @GetMapping
    @Operation(summary = "获取健康计划列表", description = "获取健康计划列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Result.class)))
    })
    @SecurityRequirement(name = "bearerAuth")
    public Result<Page<HealthPlanDTO>> getHealthPlanList(Page page , HealthPlan healthPlan) {
        log.info("获取健康计划列表");
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Page<HealthPlanDTO> healthPlans = healthPlanService.getHealthPlanList(page,healthPlan,loginUser);
        return Result.success(healthPlans);
    }

    /**
     * 获取健康计划详情
     * 所有用户都可以查看
     */
    @GetMapping("/{id}")
    @Operation(summary = "获取健康计划详情", description = "根据ID获取健康计划详情")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "404", description = "健康计划不存在")
    })
    @SecurityRequirement(name = "bearerAuth")
    public Result<HealthPlanDTO> getHealthPlanById(
            @Parameter(description = "健康计划ID") @PathVariable Long id) {
        log.info("获取健康计划详情，ID：{}", id);
        HealthPlanDTO healthPlan = healthPlanService.getHealthPlanById(id);
        return Result.success(healthPlan);
    }

    /**
     * 创建健康计划
     * 教练、管理员和学生都可以创建
     */
    @PostMapping
    @RequireRole({UserRole.COACH, UserRole.ADMIN, UserRole.STUDENT})
    @Operation(summary = "创建健康计划", description = "创建新的健康计划，教练、管理员和学生都可以操作")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "创建成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足")
    })
    @SecurityRequirement(name = "bearerAuth")
    public Result<HealthPlanDTO> createHealthPlan(
            @Valid @RequestBody CreateHealthPlanRequest request) {
        log.info("创建健康计划，请求参数：{}", request);
        HealthPlanDTO healthPlan = healthPlanService.createHealthPlan(request);
        return Result.success(healthPlan);
    }

    /**
     * 更新健康计划
     * 教练、管理员和学生都可以更新
     */
    @PutMapping("/{id}")
    @RequireRole({UserRole.COACH, UserRole.ADMIN, UserRole.STUDENT})
    @Operation(summary = "更新健康计划", description = "更新指定的健康计划，教练、管理员和学生都可以操作")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "更新成功",
                    content = @Content(schema = @Schema(implementation = Result.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "健康计划不存在")
    })
    @SecurityRequirement(name = "bearerAuth")
    public Result<HealthPlanDTO> updateHealthPlan(
            @Valid @RequestBody UpdateHealthPlanRequest request) {
        HealthPlanDTO healthPlan = healthPlanService.updateHealthPlan( request);
        return Result.success(healthPlan);
    }

    /**
     * 删除健康计划
     * 教练、管理员和学生都可以删除
     */
    @DeleteMapping("/{id}")
    @RequireRole({UserRole.COACH, UserRole.ADMIN, UserRole.STUDENT})
    @Operation(summary = "删除健康计划", description = "删除指定的健康计划，教练、管理员和学生都可以操作")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "删除成功"),
            @ApiResponse(responseCode = "403", description = "权限不足"),
            @ApiResponse(responseCode = "404", description = "健康计划不存在")
    })
    @SecurityRequirement(name = "bearerAuth")
    public Result<String> deleteHealthPlan(
            @Parameter(description = "健康计划ID") @PathVariable Long id) {
        log.info("删除健康计划，ID：{}", id);

        healthPlanService.deleteHealthPlan(id);
        return Result.success("删除成功");
    }


}
