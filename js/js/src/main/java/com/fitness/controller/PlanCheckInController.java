package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.entity.PlanCheckIn;
import com.fitness.service.PlanCheckInService;
import com.fitness.annotation.RequireRole;
import com.fitness.entity.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 健康计划打卡控制器
 */
@RestController
@RequestMapping("/plan-check-ins")
@Tag(name = "健康计划打卡管理")
@RequiredArgsConstructor
public class PlanCheckInController {

    private final PlanCheckInService planCheckInService;

    /**
     * 开始打卡
     */
    @PostMapping("/start")
    @Operation(summary = "开始打卡")
    @RequireRole({UserRole.STUDENT})
    public Result<PlanCheckIn> startCheckIn(@RequestParam Long planId, @RequestParam Long studentId, @RequestParam String exerciseType) {
        PlanCheckIn checkIn = planCheckInService.startCheckIn(planId, studentId, exerciseType);
        return Result.success(checkIn);
    }

    /**
     * 结束打卡
     */
    @PostMapping("/end/{id}")
    @Operation(summary = "结束打卡")
    @RequireRole({UserRole.STUDENT})
    public Result<PlanCheckIn> endCheckIn(@PathVariable Long id) {
        PlanCheckIn checkIn = planCheckInService.endCheckIn(id);
        return Result.success(checkIn);
    }

    /**
     * 获取计划的打卡记录
     */
    @GetMapping("/plan/{planId}")
    @Operation(summary = "获取计划的打卡记录")
    @RequireRole({UserRole.STUDENT, UserRole.COACH})
    public Result<List<PlanCheckIn>> getCheckInsByPlanId(@PathVariable Long planId) {
        List<PlanCheckIn> checkIns = planCheckInService.getCheckInsByPlanId(planId);
        return Result.success(checkIns);
    }

    /**
     * 获取计划在指定日期范围内的打卡记录
     */
    @GetMapping("/plan/{planId}/date-range")
    @Operation(summary = "获取计划在指定日期范围内的打卡记录")
    @RequireRole({UserRole.STUDENT, UserRole.COACH})
    public Result<List<PlanCheckIn>> getCheckInsByDateRange(
            @PathVariable Long planId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        List<PlanCheckIn> checkIns = planCheckInService.getCheckInsByPlanIdAndDateRange(planId, startDate, endDate);
        return Result.success(checkIns);
    }

    /**
     * 获取当天的打卡记录
     */
    @GetMapping("/today")
    @Operation(summary = "获取当天的打卡记录")
    @RequireRole({UserRole.STUDENT})
    public Result<PlanCheckIn> getTodayCheckIn(@RequestParam Long planId, @RequestParam Long studentId) {
        PlanCheckIn checkIn = planCheckInService.getTodayCheckIn(planId, studentId);
        return Result.success(checkIn);
    }

    /**
     * 计算计划的完成度
     */
    @GetMapping("/completion/{planId}")
    @Operation(summary = "计算计划的完成度")
    @RequireRole({UserRole.STUDENT, UserRole.COACH})
    public Result<Double> calculateCompletionPercentage(@PathVariable Long planId) {
        double completionPercentage = planCheckInService.calculateCompletionPercentage(planId);
        return Result.success(completionPercentage);
    }

}
