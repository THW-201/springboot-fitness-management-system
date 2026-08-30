package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.HealthPlan;
import com.fitness.entity.enums.HealthPlanStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 健康计划 Mapper 接口
 * 提供健康计划数据访问操作
 */
@Mapper
public interface HealthPlanMapper extends BaseMapper<HealthPlan> {

    /**
     * 根据学生ID查询健康计划列表
     *
     * @param studentId 学生ID
     * @return 健康计划列表
     */
    @Select("SELECT * FROM health_plans WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<HealthPlan> findByStudentId(Long studentId);

    /**
     * 根据学生ID和状态查询健康计划列表
     *
     * @param studentId 学生ID
     * @param status 计划状态
     * @return 健康计划列表
     */
    @Select("SELECT * FROM health_plans WHERE student_id = #{studentId} AND status = #{status} ORDER BY created_at DESC")
    List<HealthPlan> findByStudentIdAndStatus(Long studentId, HealthPlanStatus status);

    /**
     * 查询学生的活跃健康计划
     *
     * @param studentId 学生ID
     * @return 活跃健康计划列表
     */
    @Select("SELECT * FROM health_plans WHERE student_id = #{studentId} AND status = 'ACTIVE' ORDER BY created_at DESC")
    List<HealthPlan> findActiveByStudentId(Long studentId);

    /**
     * 查询所有活跃的健康计划
     *
     * @return 活跃健康计划列表
     */
    @Select("SELECT * FROM health_plans WHERE status = 'ACTIVE' ORDER BY created_at DESC")
    List<HealthPlan> findAllActive();
}
