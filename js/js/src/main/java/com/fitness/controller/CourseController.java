package com.fitness.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.CourseDTO;
import com.fitness.dto.CourseQueryDTO;
import com.fitness.dto.CreateCourseRequest;
import com.fitness.dto.UpdateCourseRequest;
import com.fitness.entity.Course;
import com.fitness.entity.enums.CourseStatus;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.CourseService;
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
 * 课程管理控制器
 * 提供课程的创建、更新、删除、查询等功能
 */
@Slf4j
@RestController
@RequestMapping("/courses")
@RequiredArgsConstructor
@Tag(name = "课程管理", description = "课程管理相关接口，包括课程的增删改查、搜索筛选等")
public class CourseController {

    private final CourseService courseService;

    /**
     * 获取课程列表
     * 支持分页、搜索、筛选
     *
     * @param page 分页对象
     * @param query 查询条件对象
     * @return 课程分页列表
     */
    @GetMapping
    @Operation(
            summary = "获取课程列表",
            description = "分页获取课程列表",
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
    public Result<IPage<CourseDTO>> getCourseList(
            Page page,
            Course query
    ) {
        log.info("获取课程列表: page={}, size={}, query={}", 
                page.getCurrent(), page.getSize(), query);
        
        IPage<CourseDTO> result = courseService.getCourseList(page, query);
        return Result.success(result);
    }

    /**
     * 获取课程详情
     *
     * @param id 课程ID
     * @return 课程详情
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "获取课程详情",
            description = "根据课程ID获取课程的详细信息",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = CourseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "课程不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CourseDTO> getCourseById(
            @Parameter(description = "课程ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("获取课程详情: courseId={}", id);
        CourseDTO course = courseService.getCourseById(id);
        return Result.success(course);
    }

    /**
     * 创建课程
     * 仅管理员和教练可以创建课程
     *
     * @param request 创建课程请求
     * @return 创建的课程信息
     */
    @PostMapping
//    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "创建课程",
            description = "创建新的健身课程，仅管理员和教练可以创建。系统会自动记录创建者信息。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "创建成功",
                    content = @Content(schema = @Schema(implementation = CourseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限创建课程",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CourseDTO> createCourse(
            @Parameter(description = "创建课程请求信息", required = true)
            @Valid @RequestBody CreateCourseRequest request
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long creatorId = loginUser.getUserId();
        log.info("创建课程: creatorId={}, courseName={}, coachId={}", 
                creatorId, request.getName(), request.getCoachId());
        CourseDTO course = courseService.createCourse(request, creatorId);
        return Result.success(course);
    }

    /**
     * 更新课程
     * 仅管理员和课程创建者可以更新课程
     *
     * @param id 课程ID
     * @param request 更新课程请求
     * @return 更新后的课程信息
     */
    @PutMapping("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "更新课程",
            description = "更新课程信息，仅管理员和课程创建者可以更新",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(schema = @Schema(implementation = CourseDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限更新此课程",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "课程不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CourseDTO> updateCourse(
            @Parameter(description = "课程ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "更新课程请求信息", required = true)
            @Valid @RequestBody UpdateCourseRequest request
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        log.info("更新课程: courseId={}, userId={}", id, userId);
        CourseDTO course = courseService.updateCourse(id, request, userId);
        return Result.success(course);
    }

    /**
     * 删除课程
     * 仅管理员和课程创建者可以删除课程
     * 如果课程存在未完成的预约，则不允许删除
     *
     * @param id 课程ID
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "删除课程",
            description = "删除指定课程，仅管理员和课程创建者可以删除。如果课程存在未完成的预约，则不允许删除。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "删除成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "课程存在未完成的预约，无法删除",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限删除此课程",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "课程不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<String> deleteCourse(
            @Parameter(description = "课程ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        log.info("删除课程: courseId={}, userId={}", id, userId);
        courseService.deleteCourse(id, userId);
        return Result.success("课程删除成功");
    }

}
