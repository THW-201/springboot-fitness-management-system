package com.fitness.service;

import com.fitness.dto.ReservationDTO;
import com.fitness.dto.ReserveCourseRequest;
import com.fitness.dto.ReserveEquipmentRequest;
import com.fitness.entity.Reservation;
import com.fitness.entity.enums.ReservationType;
import com.fitness.entity.enums.ReservationStatus;

import java.util.List;

/**
 * 预约服务接口
 * 提供课程预约和器材预约功能
 */
public interface ReservationService {

    /**
     * 预约课程
     * 
     * @param studentId 学生ID
     * @param request 预约课程请求
     * @return 预约信息
     */
    ReservationDTO reserveCourse(Long studentId, ReserveCourseRequest request);

    /**
     * 预约器材
     * 
     * @param studentId 学生ID
     * @param request 预约器材请求
     * @return 预约信息
     */
    ReservationDTO reserveEquipment(Long studentId, ReserveEquipmentRequest request);

    /**
     * 取消预约
     * 
     * @param reservationId 预约ID
     * @param userId 用户ID
     * @param cancelReason 取消原因
     */
    void cancelReservation(Long reservationId, Long userId, String cancelReason);

    /**
     * 获取预约详情
     * 
     * @param reservationId 预约ID
     * @return 预约信息
     */
    ReservationDTO getReservationById(Long reservationId);


    /**
     * 获取预约列表
     *
     * @param studentId 学生ID（可选，管理员可查看所有）
     * @param reservationType 预约类型（可选）
     * @param status 预约状态（可选）
     * @param keyword 搜索关键词（可选）
     * @return 预约列表
     */
    java.util.List<ReservationDTO> getReservations(Long studentId,
                                                    com.fitness.entity.enums.ReservationType reservationType,
                                                    com.fitness.entity.enums.ReservationStatus status,
                                                    String keyword);

    /**
     * 获取我的预约列表
     *
     * @return 预约列表
     */
    java.util.List<ReservationDTO> getMyReservations(Reservation reservation);
    /**
     * 根据课程ID查询报名学生
     *
     * @param courseId 课程ID
     * @return 预约列表
     */
    List<Reservation> getStudentsByCourseId(Long courseId);
    
    /**
     * 获取教练的课程预约列表
     *
     * @param coachId 教练ID
     * @param reservationType 预约类型（可选）
     * @param status 预约状态（可选）
     * @return 预约列表
     */
    List<ReservationDTO> getCoachCourseReservations(Long coachId, ReservationType reservationType, ReservationStatus status);
    
    /**
     * 确认预约
     *
     * @param reservationId 预约ID
     * @param userId 操作用户ID
     * @return 预约信息
     */
    ReservationDTO confirmReservation(Long reservationId, Long userId);
    
    /**
     * 拒绝预约
     *
     * @param reservationId 预约ID
     * @param userId 操作用户ID
     * @param rejectReason 拒绝原因
     * @return 预约信息
     */
    ReservationDTO rejectReservation(Long reservationId, Long userId, String rejectReason);
    
    /**
     * 完成预约
     *
     * @param reservationId 预约ID
     * @param userId 操作用户ID
     * @return 预约信息
     */
    ReservationDTO completeReservation(Long reservationId, Long userId);
    
    /**
     * 学生获取自己的预约列表（分页）
     *
     * @param studentId 学生ID
     * @param page 页码
     * @param pageSize 每页大小
     * @param type 预约类型（可选）
     * @param status 预约状态（可选）
     * @return 预约列表
     */
    com.fitness.common.Result<?> getStudentReservations(Long studentId, Integer page, Integer pageSize, ReservationType type, ReservationStatus status);
}
