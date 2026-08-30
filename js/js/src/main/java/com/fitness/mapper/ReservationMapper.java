package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.Reservation;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约 Mapper 接口
 * 提供预约数据访问操作
 */
@Mapper
public interface ReservationMapper extends BaseMapper<Reservation> {

    /**
     * 根据学生ID查询预约列表
     *
     * @param studentId 学生ID
     * @return 预约列表
     */
    @Select("SELECT * FROM reservations WHERE student_id = #{studentId} and reservation_type=#{reservationType} ORDER BY start_time DESC")
    List<Reservation> findByStudentId(Long studentId,ReservationType reservationType);

    /**
     * 根据课程ID查询预约列表
     *
     * @param courseId 课程ID
     * @return 预约列表
     */
    @Select("SELECT * FROM reservations WHERE course_id = #{courseId} ORDER BY created_at DESC")
    List<Reservation> findByCourseId(Long courseId);

    /**
     * 根据器材ID查询预约列表
     *
     * @param equipmentId 器材ID
     * @return 预约列表
     */
    @Select("SELECT * FROM reservations WHERE equipment_id = #{equipmentId} ORDER BY start_time DESC")
    List<Reservation> findByEquipmentId(Long equipmentId);

    /**
     * 查询指定时间范围内的预约
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 预约列表
     */
    @Select("SELECT * FROM reservations WHERE start_time < #{endTime} AND end_time > #{startTime} ORDER BY start_time")
    List<Reservation> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                      @Param("endTime") LocalDateTime endTime);

    /**
     * 检测学生时间冲突
     * 查询学生在指定时间段内是否有预约
     *
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的预约ID（用于更新时排除自己）
     * @return 冲突的预约列表
     */
    @Select("<script>" +
            "SELECT * FROM reservations " +
            "WHERE student_id = #{studentId} " +
            "AND status IN ('PENDING', 'CONFIRMED') " +
            "AND start_time &lt; #{endTime} " +
            "AND end_time &gt; #{startTime} " +
            "<if test='excludeId != null'>" +
            "AND id != #{excludeId} " +
            "</if>" +
            "<if test='reservationType != null'>" +
            "AND reservation_type = #{reservationType} " +
            "</if>" +
            "</script>")
    List<Reservation> findConflictingReservations(@Param("studentId") Long studentId,
                                                   @Param("startTime") LocalDateTime startTime,
                                                   @Param("endTime") LocalDateTime endTime,
                                                   @Param("excludeId") Long excludeId,
                                                   @Param("reservationType") ReservationType reservationType
    );

    /**
     * 检测器材时间冲突
     * 查询器材在指定时间段内是否已被预约
     *
     * @param equipmentId 器材ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeId 排除的预约ID（用于更新时排除自己）
     * @return 冲突的预约列表
     */
    @Select("<script>" +
            "SELECT * FROM reservations " +
            "WHERE equipment_id = #{equipmentId} " +
            "AND status IN ('PENDING', 'CONFIRMED') " +
            "AND start_time &lt; #{endTime} " +
            "AND end_time &gt; #{startTime} " +
            "<if test='excludeId != null'>" +
            "AND id != #{excludeId} " +
            "</if>" +
            "</script>")
    List<Reservation> findEquipmentConflicts(@Param("equipmentId") Long equipmentId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime,
                                             @Param("excludeId") Long excludeId);

    /**
     * 查询学生的有效预约（未取消、未完成）
     *
     * @param studentId 学生ID
     * @return 有效预约列表
     */
    @Select("SELECT * FROM reservations WHERE student_id = #{studentId} AND status IN ('PENDING', 'CONFIRMED') ORDER BY start_time")
    List<Reservation> findActiveReservations(Long studentId);

    /**
     * 查询课程的未完成预约数量
     *
     * @param courseId 课程ID
     * @return 未完成预约数量
     */
    @Select("SELECT COUNT(*) FROM reservations WHERE course_id = #{courseId} AND status IN ('PENDING', 'CONFIRMED')")
    int countActiveCourseReservations(Long courseId);

    /**
     * 根据预约类型和状态查询预约列表
     *
     * @param reservationType 预约类型
     * @param status 预约状态
     * @return 预约列表
     */
    @Select("SELECT * FROM reservations WHERE reservation_type = #{reservationType} AND status = #{status} ORDER BY start_time DESC")
    List<Reservation> findByTypeAndStatus(@Param("reservationType") ReservationType reservationType,
                                          @Param("status") ReservationStatus status);

    /**
     * 查询需要提醒的预约
     * 课程：开始前24小时内
     * 器材：开始前30分钟内
     *
     * @param now 当前时间
     * @param courseReminderTime 课程提醒时间（24小时后）
     * @param equipmentReminderTime 器材提醒时间（30分钟后）
     * @return 需要提醒的预约列表
     */
    @Select("SELECT * FROM reservations " +
            "WHERE status = 'CONFIRMED' " +
            "AND (" +
            "(reservation_type = 'COURSE' AND start_time > #{now} AND start_time <= #{courseReminderTime}) " +
            "OR " +
            "(reservation_type = 'EQUIPMENT' AND start_time > #{now} AND start_time <= #{equipmentReminderTime})" +
            ")")
    List<Reservation> findReservationsNeedingReminder(@Param("now") LocalDateTime now,
                                                      @Param("courseReminderTime") LocalDateTime courseReminderTime,
                                                      @Param("equipmentReminderTime") LocalDateTime equipmentReminderTime);

    /**
     * 查询学生在指定课程中的有效预约数量
     *
     * @param courseId 课程ID
     * @param studentId 学生ID
     * @return 有效预约数量
     */
    @Select("SELECT COUNT(*) FROM reservations WHERE course_id = #{courseId} AND student_id = #{studentId} AND status IN ('PENDING', 'CONFIRMED')")
    int countActiveCourseReservationsForStudent(@Param("courseId") Long courseId,
                                                 @Param("studentId") Long studentId);

    /**
     * 查询学生在指定时间段内的课程预约冲突
     *
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突的预约列表
     */
    @Select("<script>" +
            "SELECT r.* FROM reservations r " +
            "WHERE r.student_id = #{studentId} " +
            "AND r.status IN ('PENDING', 'CONFIRMED') " +
            "AND r.reservation_type = 'COURSE' " +
            "AND r.start_time &lt; #{endTime} " +
            "AND r.end_time &gt; #{startTime} " +
            "</script>")
    List<Reservation> findConflictingCourseReservations(@Param("studentId") Long studentId,
                                                         @Param("startTime") LocalDateTime startTime,
                                                         @Param("endTime") LocalDateTime endTime);
}
