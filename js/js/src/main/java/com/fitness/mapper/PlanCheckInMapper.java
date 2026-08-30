package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.PlanCheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

/**
 * 健康计划打卡Mapper
 */
@Mapper
public interface PlanCheckInMapper extends BaseMapper<PlanCheckIn> {

    /**
     * 根据计划ID和日期范围查询打卡记录
     */
    @Select("SELECT * FROM plan_check_ins WHERE plan_id = #{planId} AND check_date BETWEEN #{startDate} AND #{endDate} ORDER BY check_date DESC")
    List<PlanCheckIn> selectByPlanIdAndDateRange(Long planId, LocalDate startDate, LocalDate endDate);

    /**
     * 根据计划ID和日期查询打卡记录
     */
    @Select("SELECT * FROM plan_check_ins WHERE plan_id = #{planId} AND check_date = #{checkDate}")
    PlanCheckIn selectByPlanIdAndDate(Long planId, LocalDate checkDate);

    /**
     * 根据计划ID和状态查询打卡记录
     */
    @Select("SELECT * FROM plan_check_ins WHERE plan_id = #{planId} AND status = #{status} ORDER BY check_date DESC")
    List<PlanCheckIn> selectByPlanIdAndStatus(Long planId, String status);

}
