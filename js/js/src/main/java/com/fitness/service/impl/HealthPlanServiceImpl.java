package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.ResultCode;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.CreateHealthPlanRequest;
import com.fitness.dto.HealthPlanDTO;
import com.fitness.dto.HealthPlanQueryDTO;
import com.fitness.dto.UpdateHealthPlanProgressRequest;
import com.fitness.dto.UpdateHealthPlanRequest;
import com.fitness.entity.HealthPlan;
import com.fitness.entity.Reservation;
import com.fitness.entity.User;
import com.fitness.entity.enums.HealthPlanStatus;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.*;
import com.fitness.service.HealthPlanService;
import com.fitness.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 健康计划服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthPlanServiceImpl implements HealthPlanService {

    private final HealthPlanMapper healthPlanMapper;
    private final UserMapper userMapper;
    private final CheckInMapper checkInMapper;
    private final NotificationService notificationService;
    private final ReservationMapper reservationMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthPlanDTO createHealthPlan(CreateHealthPlanRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = userMapper.selectById(loginUser.getUserId());
        
        // 1. 验证日期
        if (request.getEndDate().isBefore(request.getStartDate())) {
            log.warn("结束日期 {} 早于开始日期 {}", request.getEndDate(), request.getStartDate());
            throw new BusinessException(ResultCode.BAD_REQUEST, "结束日期不能早于开始日期");
        }

        // 2. 确定学生ID和教练ID
        Long studentId;
        Long coachId;
        
        if ("STUDENT".equals(user.getRole().getCode())) {
            // 学生自己创建健身计划
            studentId = loginUser.getUserId();
            coachId = null; // 学生自己创建的计划没有指定教练
        } else {
            // 教练或管理员创建健身计划
            studentId = request.getStudentId();
            coachId = loginUser.getUserId();
        }

        // 3. 创建健康计划
        HealthPlan healthPlan = HealthPlan.builder()
                .studentId(studentId)
                .coachId(coachId)
                .planName(request.getPlanName())
                .description(request.getDescription())
                .targetWeight(request.getTargetWeight())
                .targetDurationMinutes(request.getTargetDurationMinutes())
                .currentWeight(request.getCurrentWeight())
                .currentDurationMinutes(0) // 初始为0
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .status(HealthPlanStatus.ACTIVE)
                .completionPercentage(BigDecimal.ZERO)
                .build();

        healthPlanMapper.insert(healthPlan);
        log.info("健康计划创建成功 - 计划ID: {}, 创建者: {}", healthPlan.getId(), loginUser.getUserId());

        // 4. 返回健康计划信息
        return convertToDTO(healthPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HealthPlanDTO updateHealthPlan( UpdateHealthPlanRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = userMapper.selectById(loginUser.getUserId());
        
        log.info("用户 {} 更新健康计划 {}", loginUser.getUserId(), request.getId());

        // 1. 查询健康计划
        HealthPlan healthPlan = healthPlanMapper.selectById( request.getId());
        if (healthPlan == null) {
            log.warn("健康计划 {} 不存在",  request.getId());
            throw new BusinessException(ResultCode.NOT_FOUND, "健康计划不存在");
        }

        // 2. 权限验证：学生只能更新自己的健康计划
        if ("STUDENT".equals(user.getRole().getCode())) {
            if (!healthPlan.getStudentId().equals(loginUser.getUserId())) {
                log.warn("学生 {} 无权更新健康计划 {}", loginUser.getUserId(), request.getId());
                throw new BusinessException(ResultCode.FORBIDDEN, "无权更新该健康计划");
            }
        }

        // 3. 更新字段
        if (request.getPlanName() != null) {
            healthPlan.setPlanName(request.getPlanName());
        }
        if (request.getDescription() != null) {
            healthPlan.setDescription(request.getDescription());
        }
        if (request.getTargetWeight() != null) {
            healthPlan.setTargetWeight(request.getTargetWeight());
        }
        if (request.getTargetDurationMinutes() != null) {
            healthPlan.setTargetDurationMinutes(request.getTargetDurationMinutes());
        }
        if (request.getCurrentWeight() != null) {
            healthPlan.setCurrentWeight(request.getCurrentWeight());
        }
        if (request.getStartDate() != null) {
            healthPlan.setStartDate(request.getStartDate());
        }
        if (request.getEndDate() != null) {
            healthPlan.setEndDate(request.getEndDate());
        }
        if (request.getStatus() != null) {
            healthPlan.setStatus(request.getStatus());
        }

        // 4. 验证日期
        if (healthPlan.getEndDate().isBefore(healthPlan.getStartDate())) {
            log.warn("结束日期 {} 早于开始日期 {}", healthPlan.getEndDate(), healthPlan.getStartDate());
            throw new BusinessException(ResultCode.BAD_REQUEST, "结束日期不能早于开始日期");
        }

        // 5. 重新计算完成百分比
        calculateCompletionPercentage(healthPlan);

        healthPlanMapper.updateById(healthPlan);

        // 6. 检查目标达成
        checkGoalAchievement(request.getId());

        // 7. 返回更新后的健康计划信息
        return convertToDTO(healthPlan);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteHealthPlan(Long planId) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = userMapper.selectById(loginUser.getUserId());

        // 1. 查询健康计划
        HealthPlan healthPlan = healthPlanMapper.selectById(planId);
        if (healthPlan == null) {
            log.warn("健康计划 {} 不存在", planId);
            throw new BusinessException(ResultCode.NOT_FOUND, "健康计划不存在");
        }

        // 2. 权限验证：学生只能删除自己的健康计划
        if ("STUDENT".equals(user.getRole().getCode())) {
            if (!healthPlan.getStudentId().equals(loginUser.getUserId())) {
                log.warn("学生 {} 无权删除健康计划 {}", loginUser.getUserId(), planId);
                throw new BusinessException(ResultCode.FORBIDDEN, "无权删除该健康计划");
            }
        }

        // 3. 删除健康计划
        healthPlanMapper.deleteById(planId);
        log.info("健康计划 {} 删除成功", planId);
    }


    @Override
    public HealthPlanDTO getHealthPlanById(Long planId) {
        log.info("查询健康计划详情: planId={}", planId);

        HealthPlan healthPlan = healthPlanMapper.selectById(planId);
        if (healthPlan == null) {
            log.warn("健康计划 {} 不存在", planId);
            throw new BusinessException(ResultCode.NOT_FOUND, "健康计划不存在");
        }

        return convertToDTO(healthPlan);
    }


    @Override
    public Page<HealthPlanDTO> getHealthPlanList(
            Page page,
            HealthPlan query,
            LoginUser loginUser) {

        // 构建查询条件
     LambdaQueryWrapper<HealthPlan> wrapper = new LambdaQueryWrapper<>();
        User user = userMapper.selectById(loginUser.getUserId());
        // 根据角色过滤数据
        if ("STUDENT".equals(user.getRole().getCode())) {
            // 学生只能查看自己的健康计划
            wrapper.eq(HealthPlan::getStudentId, loginUser.getUserId());
        } else if ("COACH".equals(user.getRole().getCode())) {
            // 教练查看自己负责的学生的健康计划
            // 通过课程预约关系查询教练负责的学生ID列表
            wrapper.eq(HealthPlan::getCoachId, loginUser.getUserId());
        }
        // 管理员可以查看所有健康计划，不需要额外过滤

        // 应用查询条件
        if (query != null) {
            // 如果指定了学生ID，则按学生ID过滤
            if (query.getStudentId() != null) {
                wrapper.eq(HealthPlan::getStudentId, query.getStudentId());
            }
            // 状态过滤
            if (query.getStatus() != null) {
                wrapper.eq(HealthPlan::getStatus, query.getStatus());
            }
        }

        // 按创建时间倒序排序
        wrapper.orderByDesc(HealthPlan::getCreatedAt);

        // 执行分页查询
       Page<HealthPlan> healthPlanPage =
                healthPlanMapper.selectPage(page, wrapper);

        // 转换为DTO
       Page<HealthPlanDTO> resultPage =
               new Page<>(
                        healthPlanPage.getCurrent(),
                        healthPlanPage.getSize(),
                        healthPlanPage.getTotal());

        List<HealthPlanDTO> dtoList = healthPlanPage.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        resultPage.setRecords(dtoList);

        log.info("查询到 {} 条健康计划记录", resultPage.getTotal());
        return resultPage;
    }
    @Override
    public void checkGoalAchievement(Long planId) {
        log.info("检查健康计划 {} 的目标达成情况", planId);

        // 1. 查询健康计划
        HealthPlan healthPlan = healthPlanMapper.selectById(planId);
        if (healthPlan == null || healthPlan.getStatus() != HealthPlanStatus.ACTIVE) {
            return;
        }

        // 2. 检查是否达成目标
        boolean weightGoalAchieved = false;
        boolean durationGoalAchieved = false;

        // 检查体重目标
        if (healthPlan.getTargetWeight() != null && healthPlan.getCurrentWeight() != null) {
            // 假设目标是减重，如果当前体重小于等于目标体重，则达成
            if (healthPlan.getCurrentWeight().compareTo(healthPlan.getTargetWeight()) <= 0) {
                weightGoalAchieved = true;
            }
        }

        // 检查运动时长目标
        if (healthPlan.getTargetDurationMinutes() != null && healthPlan.getCurrentDurationMinutes() != null) {
            if (healthPlan.getCurrentDurationMinutes() >= healthPlan.getTargetDurationMinutes()) {
                durationGoalAchieved = true;
            }
        }

        // 3. 如果所有目标都达成，更新状态并发送通知
        if ((healthPlan.getTargetWeight() == null || weightGoalAchieved) &&
            (healthPlan.getTargetDurationMinutes() == null || durationGoalAchieved)) {

            log.info("健康计划 {} 目标已达成", planId);
            healthPlan.setStatus(HealthPlanStatus.COMPLETED);
            healthPlan.setCompletionPercentage(BigDecimal.valueOf(100));
            healthPlanMapper.updateById(healthPlan);

            // 发送祝贺通知
            notificationService.sendGoalAchievement(healthPlan);
        }
    }

    /**
     * 计算完成百分比
     */
    private void calculateCompletionPercentage(HealthPlan healthPlan) {
        BigDecimal totalPercentage = BigDecimal.ZERO;
        int goalCount = 0;

        // 计算体重目标完成百分比
        if (healthPlan.getTargetWeight() != null && healthPlan.getCurrentWeight() != null) {
            BigDecimal initialWeight = healthPlan.getCurrentWeight();
            BigDecimal targetWeight = healthPlan.getTargetWeight();
            BigDecimal currentWeight = healthPlan.getCurrentWeight();

            // 假设是减重目标
            if (initialWeight.compareTo(targetWeight) > 0) {
                BigDecimal totalWeightToLose = initialWeight.subtract(targetWeight);
                BigDecimal weightLost = initialWeight.subtract(currentWeight);

                if (totalWeightToLose.compareTo(BigDecimal.ZERO) > 0) {
                    BigDecimal weightPercentage = weightLost.divide(totalWeightToLose, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
                    // 限制在0-100之间
                    weightPercentage = weightPercentage.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
                    totalPercentage = totalPercentage.add(weightPercentage);
                    goalCount++;
                }
            }
        }

        // 计算运动时长目标完成百分比
        if (healthPlan.getTargetDurationMinutes() != null && healthPlan.getCurrentDurationMinutes() != null) {
            BigDecimal durationPercentage = BigDecimal.valueOf(healthPlan.getCurrentDurationMinutes())
                    .divide(BigDecimal.valueOf(healthPlan.getTargetDurationMinutes()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
            // 限制在0-100之间
            durationPercentage = durationPercentage.max(BigDecimal.ZERO).min(BigDecimal.valueOf(100));
            totalPercentage = totalPercentage.add(durationPercentage);
            goalCount++;
        }

        // 计算平均完成百分比
        if (goalCount > 0) {
            BigDecimal averagePercentage = totalPercentage.divide(BigDecimal.valueOf(goalCount), 2, RoundingMode.HALF_UP);
            healthPlan.setCompletionPercentage(averagePercentage);
        } else {
            healthPlan.setCompletionPercentage(BigDecimal.ZERO);
        }
    }



    /**
     * 将健康计划实体转换为DTO
     */
    private HealthPlanDTO convertToDTO(HealthPlan healthPlan) {
        HealthPlanDTO dto = HealthPlanDTO.builder()
                .id(healthPlan.getId())
                .studentId(healthPlan.getStudentId())
                .planName(healthPlan.getPlanName())
                .description(healthPlan.getDescription())
                .targetWeight(healthPlan.getTargetWeight())
                .targetDurationMinutes(healthPlan.getTargetDurationMinutes())
                .currentWeight(healthPlan.getCurrentWeight())
                .currentDurationMinutes(healthPlan.getCurrentDurationMinutes())
                .startDate(healthPlan.getStartDate())
                .endDate(healthPlan.getEndDate())
                .status(healthPlan.getStatus())
                .completionPercentage(healthPlan.getCompletionPercentage())
                .createdAt(healthPlan.getCreatedAt())
                .updatedAt(healthPlan.getUpdatedAt())
                .build();

        // 填充学生姓名
        User student = userMapper.selectById(healthPlan.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName() != null ? student.getRealName() : student.getUsername());
        }

        return dto;
    }
}

