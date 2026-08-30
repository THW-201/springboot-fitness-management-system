package com.fitness.controller;

import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.CheckInDTO;
import com.fitness.dto.CheckInRequest;
import com.fitness.dto.CheckOutRequest;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.CheckInService;
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

import java.util.List;

/**
 * 签到打卡控制器
 * 提供签到、签退、查询签到记录等功能
 */
@Slf4j
@RestController
@RequestMapping("/checkins")
@RequiredArgsConstructor
@Tag(name = "签到打卡", description = "签到打卡相关接口，包括签到、签退、查询签到记录等")
public class CheckInController {

    private final CheckInService checkInService;

    /**
     * 签到
     *
     * @param request 签到请求
     * @return 签到信息
     */
    @PostMapping
    @Operation(
            summary = "签到",
            description = "学生在预约时间范围内签到。允许提前30分钟或延迟30分钟签到。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "签到成功",
                    content = @Content(schema = @Schema(implementation = CheckInDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "签到时间不在预约时间范围内或已经签到",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权签到该预约",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "预约不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CheckInDTO> checkIn(
            @Parameter(description = "签到请求信息", required = true)
            @Valid @RequestBody CheckInRequest request
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("签到: studentId={}, reservationId={}", studentId, request.getReservationId());
        CheckInDTO checkIn = checkInService.checkIn(studentId, request);
        return Result.success(checkIn);
    }

    /**
     * 签退
     *
     * @param id 签到ID
     * @param request 签退请求
     * @return 更新后的签到信息
     */
    @PutMapping("/{id}/checkout")
    @Operation(
            summary = "签退",
            description = "学生完成活动后签退。系统会自动计算活动时长和消耗卡路里。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "签退成功",
                    content = @Content(schema = @Schema(implementation = CheckInDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "该签到记录已经签退",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权签退该签到记录",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "签到记录不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CheckInDTO> checkOut(
            @Parameter(description = "签到ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "签退请求信息")
            @RequestBody(required = false) CheckOutRequest request
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("签退: checkInId={}, studentId={}", id, studentId);
        
        // 如果没有提供请求体，创建一个空的请求对象
        if (request == null) {
            request = new CheckOutRequest();
        }
        
        CheckInDTO checkIn = checkInService.checkOut(id, studentId, request);
        return Result.success(checkIn);
    }

    /**
     * 获取我的签到记录
     *
     * @return 签到记录列表
     */
    @GetMapping("/my")
    @Operation(
            summary = "获取我的签到记录",
            description = "获取当前登录用户的所有签到记录",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<List<CheckInDTO>> getMyCheckInRecords() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("获取我的签到记录: studentId={}", studentId);
        List<CheckInDTO> checkIns = checkInService.getCheckInRecords(studentId);
        return Result.success(checkIns);
    }

    /**
     * 获取签到详情
     *
     * @param id 签到ID
     * @return 签到详情
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "获取签到详情",
            description = "根据签到ID获取签到的详细信息",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = CheckInDTO.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "签到记录不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CheckInDTO> getCheckInById(
            @Parameter(description = "签到ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("获取签到详情: checkInId={}", id);
        CheckInDTO checkIn = checkInService.getCheckInById(id);
        return Result.success(checkIn);
    }

    /**
     * 获取所有签到记录（管理员/教练）
     *
     * @param studentId 学生ID（可选）
     * @param status 签到状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 签到记录列表
     */
    @GetMapping
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "获取所有签到记录",
            description = "管理员和教练获取所有学生的签到记录，支持按学生ID、状态和日期范围筛选",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = List.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "权限不足",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<List<CheckInDTO>> getAllCheckIns(
            @Parameter(description = "学生ID（可选）", example = "1")
            @RequestParam(required = false) Long studentId,

            @Parameter(description = "签到状态（可选）", example = "CHECKED_IN")
            @RequestParam(required = false) String status,

            @Parameter(description = "开始日期（可选）", example = "2024-03-01")
            @RequestParam(required = false) String startDate,

            @Parameter(description = "结束日期（可选）", example = "2024-03-31")
            @RequestParam(required = false) String endDate
    ) {
        log.info("获取所有签到记录: studentId={}, status={}, startDate={}, endDate={}", studentId, status, startDate, endDate);
        List<CheckInDTO> checkIns = checkInService.getAllCheckIns(studentId, status, startDate, endDate);
        return Result.success(checkIns);
    }

    /**
     * 获取签到统计数据（管理员/教练）
     *
     * @return 签到统计数据
     */
    @GetMapping("/statistics")
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "获取签到统计数据",
            description = "管理员和教练获取签到统计数据，包括总签到次数、已完成签到和未完成签到的数量",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "权限不足",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<java.util.Map<String, Integer>> getCheckInStatistics() {
        log.info("获取签到统计数据");
        java.util.Map<String, Integer> statistics = checkInService.getCheckInStatistics();
        return Result.success(statistics);
    }

    /**
     * 更新签到状态（管理员）
     *
     * @param id 签到ID
     * @param data 更新数据
     * @return 更新后的签到信息
     */
    @PutMapping("/{id}")
    @RequireRole(UserRole.ADMIN)
    @Operation(
            summary = "更新签到状态",
            description = "管理员更新签到状态，包括标记为已签退、设置签退时间和消耗卡路里",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "更新成功",
                    content = @Content(schema = @Schema(implementation = CheckInDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "权限不足",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "签到记录不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<CheckInDTO> updateCheckInStatus(
            @Parameter(description = "签到ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "更新数据", required = true)
            @RequestBody CheckOutRequest data
    ) {
        log.info("更新签到状态: checkInId={}, data={}", id, data);
        CheckInDTO checkIn = checkInService.updateCheckInStatus(id, data);
        return Result.success(checkIn);
    }

    /**
     * 获取未签到的预约记录
     *
     * @param studentId 学生ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 未签到的预约记录列表
     */
    @GetMapping("/unchecked")
    @RequireRole({UserRole.ADMIN, UserRole.COACH})
    @Operation(
            summary = "获取未签到的预约记录",
            description = "管理员和教练获取所有未签到的预约记录，支持按学生ID和日期范围筛选",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = java.util.List.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "权限不足",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<List<com.fitness.dto.ReservationDTO>> getUncheckedInReservations(
            @Parameter(description = "学生ID（可选）", example = "1")
            @RequestParam(required = false) Long studentId,

            @Parameter(description = "开始日期（可选）", example = "2024-03-01")
            @RequestParam(required = false) String startDate,

            @Parameter(description = "结束日期（可选）", example = "2024-03-31")
            @RequestParam(required = false) String endDate
    ) {
        log.info("查询未签到的预约记录: studentId={}, startDate={}, endDate={}", studentId, startDate, endDate);
        List<com.fitness.dto.ReservationDTO> reservations = checkInService.getUncheckedInReservations(studentId, startDate, endDate);
        return Result.success(reservations);
    }
}
