package com.fitness.service;

import com.fitness.entity.PlanCheckIn;
import com.baomidou.mybatisplus.extension.service.IService;

import java.time.LocalDate;
import java.util.List;

/**
 * 健康计划打卡服务
 */
public interface PlanCheckInService extends IService<PlanCheckIn> {

    /**
     * 开始打卡
     */
    PlanCheckIn startCheckIn(Long planId, Long studentId, String exerciseType);

    /**
     * 结束打卡
     */
    PlanCheckIn endCheckIn(Long checkInId);

    /**
     * 获取计划的打卡记录
     */
    List<PlanCheckIn> getCheckInsByPlanId(Long planId);

    /**
     * 获取计划在指定日期范围内的打卡记录
     */
    List<PlanCheckIn> getCheckInsByPlanIdAndDateRange(Long planId, LocalDate startDate, LocalDate endDate);

    /**
     * 获取当天的打卡记录
     */
    PlanCheckIn getTodayCheckIn(Long planId, Long studentId);

    /**
     * 计算计划的完成度
     */
    double calculateCompletionPercentage(Long planId);

}
