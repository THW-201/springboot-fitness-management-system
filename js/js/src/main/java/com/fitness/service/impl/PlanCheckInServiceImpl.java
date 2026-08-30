package com.fitness.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fitness.entity.HealthPlan;
import com.fitness.entity.PlanCheckIn;
import com.fitness.mapper.HealthPlanMapper;
import com.fitness.mapper.PlanCheckInMapper;
import com.fitness.service.PlanCheckInService;
import com.fitness.exception.BusinessException;
import com.fitness.common.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 健康计划打卡服务实现
 */
@Service
public class PlanCheckInServiceImpl extends ServiceImpl<PlanCheckInMapper, PlanCheckIn> implements PlanCheckInService {

    @Autowired
    private HealthPlanMapper healthPlanMapper;

    // 运动类型卡路里消耗系数（卡路里/分钟）
    private static final Map<String, Double> CALORIES_PER_MINUTE = new HashMap<>();

    static {
        CALORIES_PER_MINUTE.put("跑步", 10.0);
        CALORIES_PER_MINUTE.put("慢跑", 8.0);
        CALORIES_PER_MINUTE.put("游泳", 12.0);
        CALORIES_PER_MINUTE.put("健身", 7.0);
        CALORIES_PER_MINUTE.put("瑜伽", 3.0);
        CALORIES_PER_MINUTE.put("篮球", 9.0);
        CALORIES_PER_MINUTE.put("足球", 10.0);
        CALORIES_PER_MINUTE.put("羽毛球", 7.0);
        CALORIES_PER_MINUTE.put("乒乓球", 5.0);
        CALORIES_PER_MINUTE.put("骑行", 8.0);
        CALORIES_PER_MINUTE.put("跳绳", 12.0);
        CALORIES_PER_MINUTE.put("爬山", 9.0);
        CALORIES_PER_MINUTE.put("跳舞", 6.0);
        CALORIES_PER_MINUTE.put("其他", 5.0);
    }

    @Override
    @Transactional
    public PlanCheckIn startCheckIn(Long planId, Long studentId, String exerciseType) {
        // 检查是否已经有进行中的打卡
        PlanCheckIn todayCheckIn = getTodayCheckIn(planId, studentId);
        if (todayCheckIn != null && "IN_PROGRESS".equals(todayCheckIn.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "今日已有进行中的打卡");
        }

        // 创建新的打卡记录
        PlanCheckIn checkIn = PlanCheckIn.builder()
                .planId(planId)
                .studentId(studentId)
                .checkDate(LocalDate.now())
                .checkInTime(LocalDateTime.now())
                .status("IN_PROGRESS")
                .exerciseType(exerciseType)
                .build();

        save(checkIn);
        return checkIn;
    }

    @Override
    @Transactional
    public PlanCheckIn endCheckIn(Long checkInId) {
        PlanCheckIn checkIn = getById(checkInId);
        if (checkIn == null) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "打卡记录不存在");
        }

        if (!"IN_PROGRESS".equals(checkIn.getStatus())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "打卡已结束");
        }

        // 计算打卡时长
        LocalDateTime checkInTime = checkIn.getCheckInTime();
        LocalDateTime checkOutTime = LocalDateTime.now();
        long durationMinutes = ChronoUnit.MINUTES.between(checkInTime, checkOutTime);

        // 计算消耗的卡路里
        Double caloriesBurned = calculateCalories(checkIn.getExerciseType(), durationMinutes);

        // 更新打卡记录
        checkIn.setCheckOutTime(checkOutTime);
        checkIn.setDurationMinutes((int) durationMinutes);
        checkIn.setStatus("COMPLETED");
        checkIn.setCaloriesBurned(caloriesBurned);

        updateById(checkIn);

        // 更新健康计划的完成度
        updateHealthPlanCompletion(checkIn.getPlanId());

        return checkIn;
    }

    /**
     * 根据运动类型和时长计算消耗的卡路里
     */
    private Double calculateCalories(String exerciseType, long durationMinutes) {
        if (exerciseType == null || exerciseType.isEmpty()) {
            return 0.0;
        }
        
        // 获取该运动类型的卡路里消耗系数，默认为5.0
        Double caloriesPerMinute = CALORIES_PER_MINUTE.getOrDefault(exerciseType, 5.0);
        
        // 计算总消耗卡路里
        return caloriesPerMinute * durationMinutes;
    }

    @Override
    public List<PlanCheckIn> getCheckInsByPlanId(Long planId) {
        return getBaseMapper().selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanCheckIn>()
                .eq(PlanCheckIn::getPlanId, planId).orderByDesc(PlanCheckIn::getCheckDate)
        );
    }

    @Override
    public List<PlanCheckIn> getCheckInsByPlanIdAndDateRange(Long planId, LocalDate startDate, LocalDate endDate) {
        return getBaseMapper().selectByPlanIdAndDateRange(planId, startDate, endDate);
    }

    @Override
    public PlanCheckIn getTodayCheckIn(Long planId, Long studentId) {
        List<PlanCheckIn> checkIns = getBaseMapper().selectList(
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<PlanCheckIn>()
                .eq(PlanCheckIn::getPlanId, planId)
                .eq(PlanCheckIn::getStudentId, studentId)
                .eq(PlanCheckIn::getCheckDate, LocalDate.now())
                .orderByDesc(PlanCheckIn::getCheckInTime)
        );
        return checkIns.isEmpty() ? null : checkIns.get(0);
    }

    @Override
    public double calculateCompletionPercentage(Long planId) {
        HealthPlan plan = healthPlanMapper.selectById(planId);
        if (plan == null) {
            return 0;
        }

        // 计算计划总天数
        long totalDays = ChronoUnit.DAYS.between(plan.getStartDate(), plan.getEndDate()) + 1;
        if (totalDays <= 0) {
            return 0;
        }

        // 计算已打卡天数
        List<PlanCheckIn> checkIns = getBaseMapper().selectByPlanIdAndStatus(planId, "COMPLETED");
        long completedDays = checkIns.size();

        // 计算完成度
        return Math.min(100, (completedDays * 100.0) / totalDays);
    }

    /**
     * 更新健康计划的完成度和当前运动时长
     */
    private void updateHealthPlanCompletion(Long planId) {
        HealthPlan plan = healthPlanMapper.selectById(planId);
        if (plan != null) {
            // 计算完成度
            double completionPercentage = calculateCompletionPercentage(planId);
            plan.setCompletionPercentage(java.math.BigDecimal.valueOf(completionPercentage));
            
            // 计算当前运动时长（所有已完成打卡的总时长）
            List<PlanCheckIn> completedCheckIns = getBaseMapper().selectByPlanIdAndStatus(planId, "COMPLETED");
            int totalDurationMinutes = completedCheckIns.stream()
                    .mapToInt(PlanCheckIn::getDurationMinutes)
                    .sum();
            plan.setCurrentDurationMinutes(totalDurationMinutes);
            
            healthPlanMapper.updateById(plan);
        }
    }

}
