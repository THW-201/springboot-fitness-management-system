package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.CheckIn;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 签到 Mapper 接口
 * 提供签到数据访问操作
 */
@Mapper
public interface CheckInMapper extends BaseMapper<CheckIn> {

    /**
     * 根据预约ID查询签到记录
     *
     * @param reservationId 预约ID
     * @return 签到记录
     */
    @Select("SELECT * FROM check_ins WHERE reservation_id = #{reservationId}")
    Optional<CheckIn> findByReservationId(Long reservationId);

    /**
     * 根据学生ID查询签到记录列表
     *
     * @param studentId 学生ID
     * @return 签到记录列表
     */
    @Select("SELECT * FROM check_ins WHERE student_id = #{studentId} ORDER BY check_in_time DESC")
    List<CheckIn> findByStudentId(Long studentId);

    /**
     * 查询学生在指定时间范围内的签到记录
     *
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 签到记录列表
     */
    @Select("SELECT * FROM check_ins WHERE student_id = #{studentId} " +
            "AND check_in_time >= #{startTime} AND check_in_time <= #{endTime} " +
            "ORDER BY check_in_time DESC")
    List<CheckIn> findByStudentIdAndTimeRange(@Param("studentId") Long studentId,
                                               @Param("startTime") LocalDateTime startTime,
                                               @Param("endTime") LocalDateTime endTime);

    /**
     * 统计学生的总运动时长（分钟）
     *
     * @param studentId 学生ID
     * @return 总运动时长
     */
    @Select("SELECT COALESCE(SUM(duration_minutes), 0) FROM check_ins WHERE student_id = #{studentId}")
    Integer sumDurationByStudentId(Long studentId);

    /**
     * 统计学生在指定时间范围内的运动时长（分钟）
     *
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 运动时长
     */
    @Select("SELECT COALESCE(SUM(duration_minutes), 0) FROM check_ins " +
            "WHERE student_id = #{studentId} " +
            "AND check_in_time >= #{startTime} AND check_in_time <= #{endTime}")
    Integer sumDurationByStudentIdAndTimeRange(@Param("studentId") Long studentId,
                                                @Param("startTime") LocalDateTime startTime,
                                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计学生的总消耗卡路里
     *
     * @param studentId 学生ID
     * @return 总消耗卡路里
     */
    @Select("SELECT COALESCE(SUM(calories_burned), 0) FROM check_ins WHERE student_id = #{studentId}")
    Double sumCaloriesByStudentId(Long studentId);

    /**
     * 统计学生的签到次数
     *
     * @param studentId 学生ID
     * @return 签到次数
     */
    @Select("SELECT COUNT(*) FROM check_ins WHERE student_id = #{studentId}")
    Integer countByStudentId(Long studentId);

    /**
     * 查询所有签到记录（管理员）
     *
     * @param studentId 学生ID（可选）
     * @param status 签到状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 签到记录列表
     */
    List<CheckIn> findAll(@Param("studentId") Long studentId,
                          @Param("status") String status,
                          @Param("startDate") String startDate,
                          @Param("endDate") String endDate);

    /**
     * 查询未签到的预约记录
     *
     * @param studentId 学生ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 未签到的预约记录列表
     */
    List<com.fitness.entity.Reservation> findUncheckedInReservations(@Param("studentId") Long studentId,
                                                                    @Param("startDate") String startDate,
                                                                    @Param("endDate") String endDate);
}
