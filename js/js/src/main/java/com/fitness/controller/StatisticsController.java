package com.fitness.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.annotation.RequireRole;
import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.*;
import com.fitness.entity.enums.UserRole;
import com.fitness.service.StatisticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计控制器
 */
@RestController
@RequestMapping("/statistics")
@RequiredArgsConstructor
@Tag(name = "统计管理")
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "课程统计")
    @GetMapping("/courses")
    public Result<List<CourseStatisticsDTO>> getCourseStatistics(StatisticsQueryDTO query) {
        return Result.success(statisticsService.getCourseStatistics(query));
    }

    @Operation(summary = "器材统计")
    @GetMapping("/equipment")
    public Result<List<EquipmentStatisticsDTO>> getEquipmentStatistics(StatisticsQueryDTO query) {
        return Result.success(statisticsService.getEquipmentStatistics(query));
    }

    @Operation(summary = "学生统计")
    @GetMapping("/students")
    public Result<Page<StudentStatisticsDTO>> getStudentStatistics(Page page,StatisticsQueryDTO query) {
        return Result.success(statisticsService.getStudentStatistics(page,query));
    }

    @Operation(summary = "个人健康统计")
    @GetMapping("/my")
    public Result<PersonalHealthDataDTO> getPersonalHealthData() {

        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();

        return Result.success(statisticsService.getPersonalHealthData(studentId));
    }

}
