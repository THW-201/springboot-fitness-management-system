package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.dto.CreateEquipmentRequest;
import com.fitness.dto.EquipmentDTO;
import com.fitness.dto.EquipmentQueryDTO;
import com.fitness.dto.UpdateEquipmentRequest;
import com.fitness.entity.enums.EquipmentStatus;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.EquipmentService;
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
import org.springframework.web.bind.annotation.*;

/**
 * 器材管理控制器
 * 提供器材的创建、更新、删除、查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/equipment")
@RequiredArgsConstructor
@Tag(name = "器材管理", description = "器材管理相关接口，包括器材的增删改查、搜索筛选等")
public class EquipmentController {

    private final EquipmentService equipmentService;

    /**
     * 获取器材列表
     * 支持分页、搜索、筛选
     *
     * @param page 分页对象
     * @param query 查询条件对象
     * @return 器材分页列表
     */
    @GetMapping
    @Operation(
            summary = "获取器材列表",
            description = "分页获取器材列表，支持按器材名称、类型进行模糊搜索，支持按器材状态进行筛选",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = IPage.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<IPage<EquipmentDTO>> getEquipmentList(
            Page<EquipmentDTO> page,
            EquipmentQueryDTO query
    ) {
        log.info("获取器材列表: page={}, size={}, query={}", 
                page.getCurrent(), page.getSize(), query);
        
        IPage<EquipmentDTO> result = equipmentService.getEquipmentList(page, query);
        return Result.success(result);
    }

    /**
     * 获取器材详情
     *
     * @param id 器材ID
     * @return 器材详情
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "获取器材详情",
            description = "根据器材ID获取器材的详细信息，包括实时可用状态",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = EquipmentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "器材不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<EquipmentDTO> getEquipmentById(
            @Parameter(description = "器材ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("获取器材详情: equipmentId={}", id);
        EquipmentDTO equipment = equipmentService.getEquipmentById(id);
        return Result.success(equipment);
    }

    /**
     * 添加器材
     * 仅管理员可以添加器材
     *
     * @param request 创建器材请求
     * @return 创建的器材信息
     */
    @PostMapping
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "添加器材",
            description = "添加新的健身器材，管理员和教练可以添加。系统会自动设置器材初始状态为可用。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "添加成功",
                    content = @Content(schema = @Schema(implementation = EquipmentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限添加器材",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<EquipmentDTO> addEquipment(
            @Parameter(description = "创建器材请求信息", required = true)
            @Valid @RequestBody CreateEquipmentRequest request
    ) {
        log.info("添加器材: equipmentName={}, equipmentType={}", 
                request.getName(), request.getEquipmentType());
        EquipmentDTO equipment = equipmentService.addEquipment(request);
        return Result.success(equipment);
    }

    /**
     * 更新器材
     * 仅管理员可以更新器材
     *
     * @param id 器材ID
     * @param request 更新器材请求
     * @return 更新后的器材信息
     */
    @PutMapping("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "更新器材",
            description = "更新器材信息，管理员和教练可以更新。状态变更时会自动记录时间戳。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(schema = @Schema(implementation = EquipmentDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限更新器材",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "器材不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<EquipmentDTO> updateEquipment(
            @Parameter(description = "器材ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "更新器材请求信息", required = true)
            @Valid @RequestBody UpdateEquipmentRequest request
    ) {
        log.info("更新器材: equipmentId={}", id);
        EquipmentDTO equipment = equipmentService.updateEquipment(id, request);
        return Result.success(equipment);
    }

    /**
     * 删除器材
     * 仅管理员可以删除器材
     *
     * @param id 器材ID
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "删除器材",
            description = "删除指定器材，管理员和教练可以删除。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "删除成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限删除器材",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "器材不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<String> deleteEquipment(
            @Parameter(description = "器材ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("删除器材: equipmentId={}", id);
        equipmentService.deleteEquipment(id);
        return Result.success("器材删除成功");
    }
}
