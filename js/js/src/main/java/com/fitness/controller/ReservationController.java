package com.fitness.controller;

import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.ReservationDTO;
import com.fitness.dto.ReserveCourseRequest;
import com.fitness.dto.ReserveEquipmentRequest;
import com.fitness.entity.Reservation;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.ReservationService;
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
import java.util.Map;

/**
 * 预约管理控制器
 * 提供课程预约、器材预约、取消预约等功能
 */
@Slf4j
@RestController
@RequestMapping("/reservations")
@RequiredArgsConstructor
@Tag(name = "预约管理", description = "预约管理相关接口，包括课程预约、器材预约、取消预约等")
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * 获取预约列表
     * 管理员可以查看所有预约，学生只能查看自己的预约
     *
     * @param studentId 学生ID（可选，管理员可指定）
     * @param reservationType 预约类型（可选）
     * @param status 预约状态（可选）
     * @return 预约列表
     */
    @GetMapping
    @Operation(
            summary = "获取预约列表",
            description = "获取预约列表，支持按学生ID、预约类型、状态筛选。管理员可以查看所有预约，学生只能查看自己的预约。",
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
    public Result<List<ReservationDTO>> getReservations(
            @Parameter(description = "学生ID（可选，管理员可指定）", example = "1")
            @RequestParam(required = false) Long studentId,

            @Parameter(description = "预约类型", example = "COURSE", schema = @Schema(allowableValues = {"COURSE", "EQUIPMENT"}))
            @RequestParam(required = false) ReservationType reservationType,

            @Parameter(description = "预约状态", example = "CONFIRMED", schema = @Schema(allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"}))
            @RequestParam(required = false) ReservationStatus status,

            @Parameter(description = "搜索关键词", example = "张三")
            @RequestParam(required = false) String keyword
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        List<String> roles = loginUser.getRoles();
        log.info("获取预约列表: userId={}, roles={}, studentId={}, reservationType={}, status={}, keyword={}", 
                userId, roles, studentId, reservationType, status, keyword);
        
        // 检查用户角色
        boolean isAdmin = SecurityUtils.isAdmin();
        boolean isCoach = roles != null && roles.contains("ROLE_COACH");
        boolean isStudent = roles != null && roles.contains("ROLE_STUDENT");
        
        List<ReservationDTO> reservations;
        
        if (isAdmin) {
            // 管理员可以查看所有预约
            reservations = reservationService.getReservations(studentId, reservationType, status, keyword);
        } else if (isCoach) {
            // 教练只能查看自己课程的预约
            reservations = reservationService.getCoachCourseReservations(userId, reservationType, status);
        } else if (isStudent) {
            // 学生只能查看自己的预约
            if (studentId == null) {
                studentId = userId;
            }
            reservations = reservationService.getReservations(studentId, reservationType, status, keyword);
        } else {
            // 其他角色，只能查看自己的预约
            if (studentId == null) {
                studentId = userId;
            }
            reservations = reservationService.getReservations(studentId, reservationType, status, keyword);
        }
        
        return Result.success(reservations);
    }

    /**
     * 获取预约详情
     *
     * @param id 预约ID
     * @return 预约详情
     */
    @GetMapping("/{id}")
    @Operation(
            summary = "获取预约详情",
            description = "根据预约ID获取预约的详细信息",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = ReservationDTO.class))
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
    public Result<ReservationDTO> getReservationById(
            @Parameter(description = "预约ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        log.info("获取预约详情: reservationId={}", id);
        ReservationDTO reservation = reservationService.getReservationById(id);
        return Result.success(reservation);
    }

    /**
     * 预约课程
     *
     * @param request 预约课程请求
     * @return 预约信息
     */
    @PostMapping("/course")
    @RequireRole({UserRole.STUDENT})
    @Operation(
            summary = "预约课程",
            description = "学生预约健身课程。系统会检查课程容量限制和时间冲突。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "预约成功",
                    content = @Content(schema = @Schema(implementation = ReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "课程已满员或存在时间冲突",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "课程不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限预约课程",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<ReservationDTO> reserveCourse(
            @Parameter(description = "预约课程请求信息", required = true)
            @Valid @RequestBody ReserveCourseRequest request
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("预约课程: studentId={}, courseId={}", studentId, request.getCourseId());
        ReservationDTO reservation = reservationService.reserveCourse(studentId, request);
        return Result.success(reservation);
    }

    /**
     * 预约器材
     *
     * @param request 预约器材请求
     * @return 预约信息
     */
    @PostMapping("/equipment")
    @RequireRole({UserRole.STUDENT})
    @Operation(
            summary = "预约器材",
            description = "学生预约健身器材。系统会检查器材可用性和时间冲突。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "预约成功",
                    content = @Content(schema = @Schema(implementation = ReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "器材已被预约或存在时间冲突",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "器材不存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限预约器材",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<ReservationDTO> reserveEquipment(
            @Parameter(description = "预约器材请求信息", required = true)
            @Valid @RequestBody ReserveEquipmentRequest request
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("预约器材: studentId={}, equipmentId={}, startTime={}, endTime={}", 
                studentId, request.getEquipmentId(), request.getStartTime(), request.getEndTime());
        ReservationDTO reservation = reservationService.reserveEquipment(studentId, request);
        return Result.success(reservation);
    }

    /**
     * 取消预约
     *
     * @param id 预约ID
     * @param cancelReason 取消原因（可选）
     * @return 成功消息
     */
    @DeleteMapping("/{id}")
    @Operation(
            summary = "取消预约",
            description = "取消指定的预约。如果取消时间距离开始时间少于2小时，将记录迟到取消次数。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "取消成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "预约已取消或已完成",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限取消此预约",
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
    public Result<String> cancelReservation(
            @Parameter(description = "预约ID", example = "1", required = true)
            @PathVariable Long id,

            @Parameter(description = "取消原因", example = "临时有事")
            @RequestBody(required = false) Map<String, String> requestBody
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        String cancelReason = requestBody != null ? requestBody.get("cancelReason") : null;
        log.info("取消预约: reservationId={}, userId={}, cancelReason={}", id, userId, cancelReason);
        reservationService.cancelReservation(id, userId, cancelReason);
        return Result.success("预约取消成功");
    }

    /**
     * 获取我的预约列表
     *
     * @return 我的预约列表
     */
    @GetMapping("/my")
    @Operation(
            summary = "获取我的预约列表",
            description = "获取当前登录用户的所有预约记录",
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
    public Result<List<ReservationDTO>> getMyReservations(Reservation reservation) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("获取我的预约列表: studentId={}", studentId);
        reservation.setStudentId(studentId);
        List<ReservationDTO> reservations = reservationService.getMyReservations( reservation);
        return Result.success(reservations);
    }

    /**
     * 确认预约
     *
     * @param id 预约ID
     * @return 预约信息
     */
    @PutMapping("/{id}/confirm")
    @Operation(
            summary = "确认预约",
            description = "确认指定的预约。只有教练可以确认自己课程的预约。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "确认成功",
                    content = @Content(schema = @Schema(implementation = ReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "预约状态不是待确认",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限确认此预约",
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
    public Result<ReservationDTO> confirmReservation(
            @Parameter(description = "预约ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        log.info("确认预约: reservationId={}, userId={}", id, userId);
        ReservationDTO reservation = reservationService.confirmReservation(id, userId);
        return Result.success(reservation);
    }

    /**
     * 拒绝预约
     *
     * @param id 预约ID
     * @param rejectReason 拒绝原因
     * @return 预约信息
     */
    @PutMapping("/{id}/reject")
    @Operation(
            summary = "拒绝预约",
            description = "拒绝指定的预约。只有教练可以拒绝自己课程的预约。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "拒绝成功",
                    content = @Content(schema = @Schema(implementation = ReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "预约状态不是待确认",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限拒绝此预约",
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
    public Result<ReservationDTO> rejectReservation(
            @Parameter(description = "预约ID", example = "1", required = true)
            @PathVariable Long id,
            
            @Parameter(description = "拒绝原因", example = "课程已满")
            @RequestParam(required = false) String rejectReason
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        log.info("拒绝预约: reservationId={}, userId={}, reason={}", id, userId, rejectReason);
        ReservationDTO reservation = reservationService.rejectReservation(id, userId, rejectReason);
        return Result.success(reservation);
    }

    /**
     * 完成预约
     *
     * @param id 预约ID
     * @return 预约信息
     */
    @PutMapping("/{id}/complete")
    @Operation(
            summary = "完成预约",
            description = "完成指定的预约。只有教练可以完成自己课程的预约。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "完成成功",
                    content = @Content(schema = @Schema(implementation = ReservationDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "预约状态不是已确认",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "无权限完成此预约",
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
    public Result<ReservationDTO> completeReservation(
            @Parameter(description = "预约ID", example = "1", required = true)
            @PathVariable Long id
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        log.info("完成预约: reservationId={}, userId={}", id, userId);
        ReservationDTO reservation = reservationService.completeReservation(id, userId);
        return Result.success(reservation);
    }

    /**
     * 查询课程报名学生
     */
    @GetMapping("/course/{courseId}/students")
    @Operation(summary = "查询课程报名学生")
    public Result<List<Reservation>> getStudentsByCourseId(@PathVariable Long courseId) {
        List<Reservation> studentsByCourseId = reservationService.getStudentsByCourseId(courseId);
        return Result.success(studentsByCourseId);
    }

    /**
     * 学生获取自己的预约列表（分页）
     */
    @GetMapping("/student")
    @Operation(
            summary = "学生获取自己的预约列表",
            description = "学生获取自己的预约列表，支持分页和筛选",
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
            )
    })
    public Result<?> getStudentReservations(
            @Parameter(description = "页码", example = "1")
            @RequestParam(defaultValue = "1") Integer page,

            @Parameter(description = "每页大小", example = "10")
            @RequestParam(defaultValue = "10") Integer pageSize,

            @Parameter(description = "预约类型", example = "COURSE", schema = @Schema(allowableValues = {"COURSE", "EQUIPMENT"}))
            @RequestParam(required = false) ReservationType type,

            @Parameter(description = "预约状态", example = "CONFIRMED", schema = @Schema(allowableValues = {"PENDING", "CONFIRMED", "CANCELLED", "COMPLETED"}))
            @RequestParam(required = false) ReservationStatus status
    ) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        log.info("学生获取预约列表: studentId={}, page={}, pageSize={}, type={}, status={}", 
                studentId, page, pageSize, type, status);
        return reservationService.getStudentReservations(studentId, page, pageSize, type, status);
    }
}
