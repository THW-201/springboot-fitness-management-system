package com.fitness.service;

import com.fitness.dto.CheckInDTO;
import com.fitness.dto.CheckInRequest;
import com.fitness.dto.CheckOutRequest;

import java.util.List;

/**
 * 签到服务接口
 * 提供签到、签退、查询签到记录等功能
 */
public interface CheckInService {

    /**
     * 签到
     * 
     * @param studentId 学生ID
     * @param request 签到请求
     * @return 签到信息
     */
    CheckInDTO checkIn(Long studentId, CheckInRequest request);

    /**
     * 签退
     * 
     * @param reservationId 预约id
     * @param studentId 学生ID
     * @param request 签退请求
     * @return 更新后的签到信息
     */
    CheckInDTO checkOut(Long reservationId, Long studentId, CheckOutRequest request);

    /**
     * 获取签到记录列表
     * 
     * @param studentId 学生ID
     * @return 签到记录列表
     */
    List<CheckInDTO> getCheckInRecords(Long studentId);

    /**
     * 获取签到详情
     * 
     * @param checkInId 签到ID
     * @return 签到信息
     */
    CheckInDTO getCheckInById(Long checkInId);

    /**
     * 获取所有签到记录（管理员）
     * 
     * @param studentId 学生ID（可选）
     * @param status 签到状态（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 签到记录列表
     */
    List<CheckInDTO> getAllCheckIns(Long studentId, String status, String startDate, String endDate);

    /**
     * 获取签到统计数据（管理员）
     * 
     * @return 签到统计数据
     */
    java.util.Map<String, Integer> getCheckInStatistics();

    /**
     * 更新签到状态（管理员）
     * 
     * @param checkInId 签到ID
     * @param request 签退请求
     * @return 更新后的签到信息
     */
    CheckInDTO updateCheckInStatus(Long checkInId, CheckOutRequest request);

    /**
     * 获取未签到的预约记录
     * 
     * @param studentId 学生ID（可选）
     * @param startDate 开始日期（可选）
     * @param endDate 结束日期（可选）
     * @return 未签到的预约记录列表
     */
    List<com.fitness.dto.ReservationDTO> getUncheckedInReservations(Long studentId, String startDate, String endDate);
}
