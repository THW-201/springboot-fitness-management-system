package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.dto.CreateHealthPlanRequest;
import com.fitness.dto.HealthPlanDTO;
import com.fitness.dto.HealthPlanQueryDTO;
import com.fitness.dto.UpdateHealthPlanProgressRequest;
import com.fitness.dto.UpdateHealthPlanRequest;
import com.fitness.entity.HealthPlan;

import java.util.List;

/**
 * 健康计划服务接口
 * 提供健康计划的创建、更新、删除、进度跟踪等功能
 */
public interface HealthPlanService {

    /**
     * 创建健康计划
     * 

     * @param request 创建健康计划请求
     * @return 健康计划信息
     */
    HealthPlanDTO createHealthPlan( CreateHealthPlanRequest request);

    /**
     * 更新健康计划
     * 
     * @param request 更新健康计划请求
     * @return 更新后的健康计划信息
     */
    HealthPlanDTO updateHealthPlan( UpdateHealthPlanRequest request);

    /**
     * 删除健康计划
     * 
     * @param planId 健康计划ID
     */
    void deleteHealthPlan(Long planId);

    /**
     * 获取健康计划详情
     * 
     * @param planId 健康计划ID
     * @return 健康计划信息
     */
    HealthPlanDTO getHealthPlanById(Long planId);



    /**
     * 分页查询健康计划列表
     * 
     * @param page 分页参数
     * @param query 查询条件
     * @return 分页结果
     */
    Page<HealthPlanDTO> getHealthPlanList(Page page, HealthPlan query, LoginUser loginUser);




    /**
     * 检测目标达成并发送通知
     * 
     * @param planId 健康计划ID
     */
    void checkGoalAchievement(Long planId);
}
