package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fitness.common.ResultCode;
import com.fitness.dto.CheckInDTO;
import com.fitness.dto.CheckInRequest;
import com.fitness.dto.CheckOutRequest;
import com.fitness.entity.CheckIn;
import com.fitness.entity.Reservation;
import com.fitness.entity.StudentProfile;
import com.fitness.entity.User;
import com.fitness.entity.Course;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.CheckInMapper;
import com.fitness.mapper.ReservationMapper;
import com.fitness.mapper.StudentProfileMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.mapper.CourseMapper;
import com.fitness.service.CheckInService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
 * 签到服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CheckInServiceImpl implements CheckInService {

    private final CheckInMapper checkInMapper;
    private final ReservationMapper reservationMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckInDTO checkIn(Long studentId, CheckInRequest request) {
        log.info("学生 {} 签到，预约ID: {}", studentId, request.getReservationId());
        
        // 1. 查询预约记录
        Reservation reservation = reservationMapper.selectById(request.getReservationId());
        if (reservation == null) {
            log.warn("预约 {} 不存在", request.getReservationId());
            throw new BusinessException(ResultCode.NOT_FOUND, "预约不存在");
        }
        
        // 2. 验证预约是否属于该学生
        if (!reservation.getStudentId().equals(studentId)) {
            log.warn("学生 {} 无权签到预约 {}", studentId, request.getReservationId());
            throw new BusinessException(ResultCode.FORBIDDEN, "无权签到该预约");
        }
        
        // 3. 验证预约时间范围
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = reservation.getStartTime();
        LocalDateTime endTime = reservation.getEndTime();
        
        // 允许提前30分钟签到，延迟30分钟签到
        LocalDateTime earliestCheckIn = startTime.minusMinutes(30);
        LocalDateTime latestCheckIn = endTime.plusMinutes(30);
        
        if (now.isBefore(earliestCheckIn) || now.isAfter(latestCheckIn)) {
            log.warn("学生 {} 签到时间 {} 不在预约时间范围内 [{}, {}]", 
                     studentId, now, earliestCheckIn, latestCheckIn);
            throw new BusinessException(ResultCode.BAD_REQUEST, 
                    "签到时间不在预约时间范围内，请在预约开始前30分钟至结束后30分钟内签到");
        }
        
        // 4. 检查是否已经签到
        Optional<CheckIn> existingCheckIn = checkInMapper.findByReservationId(request.getReservationId());
        if (existingCheckIn.isPresent()) {
            log.warn("预约 {} 已经签到", request.getReservationId());
            throw new BusinessException(ResultCode.BAD_REQUEST, "该预约已经签到");
        }
        StudentProfile profile = studentProfileMapper.selectOne(Wrappers.lambdaQuery(StudentProfile.class)
                .eq(StudentProfile::getUserId, studentId).last("limit 1"));

        // 5. 创建签到记录
        CheckIn checkIn = CheckIn.builder()
                .reservationId(request.getReservationId())
                .studentId(studentId)
                .coachId(profile.getCoachId())
                .checkInTime(now)
                .location(request.getLocation())
                .build();
        
        checkInMapper.insert(checkIn);
        log.info("签到成功 - 签到ID: {}", checkIn.getId());
        
        // 6. 返回签到信息
        return convertToDTO(checkIn);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckInDTO checkOut(Long reservationId, Long studentId, CheckOutRequest request) {
        log.info("学生 {} 签退，签到ID: {}", studentId, reservationId);
        
        // 1. 查询签到记录
        CheckIn checkIn = checkInMapper.selectOne(Wrappers.lambdaQuery(CheckIn.class)
                .eq(CheckIn::getReservationId,reservationId)
                .last("limit 1"));
        if (checkIn == null) {
            log.warn("签到记录 {} 不存在", reservationId);
            throw new BusinessException(ResultCode.NOT_FOUND, "签到记录不存在");
        }
        
        // 2. 验证签到记录是否属于该学生
        if (!checkIn.getStudentId().equals(studentId)) {
            log.warn("学生 {} 无权签退签到记录 {}", studentId, reservationId);
            throw new BusinessException(ResultCode.FORBIDDEN, "无权签退该签到记录");
        }
        
        // 3. 检查是否已经签退
        if (checkIn.getCheckOutTime() != null) {
            log.warn("签到记录 {} 已经签退", reservationId);
            throw new BusinessException(ResultCode.BAD_REQUEST, "该签到记录已经签退");
        }
        
        // 4. 更新签退时间和计算活动时长
        LocalDateTime now = LocalDateTime.now();
        checkIn.setCheckOutTime(now);
        
        // 计算活动时长（分钟）
        Duration duration = Duration.between(checkIn.getCheckInTime(), now);
        int durationMinutes = (int) duration.toMinutes();
        checkIn.setDurationMinutes(durationMinutes);
        
        // 5. 设置消耗卡路里
        if (request.getCaloriesBurned() != null) {
            checkIn.setCaloriesBurned(request.getCaloriesBurned());
        } else {
            // 简单估算：每分钟消耗5卡路里
            BigDecimal estimatedCalories = BigDecimal.valueOf(durationMinutes * 5);
            checkIn.setCaloriesBurned(estimatedCalories);
        }
        
        checkInMapper.updateById(checkIn);
        log.info("签退成功 - 签到ID: {}, 活动时长: {} 分钟, 消耗卡路里: {}",
                reservationId, durationMinutes, checkIn.getCaloriesBurned());
        
        // 6. 将签到数据关联到健康数据统计
        // 这里可以触发健康计划的自动更新，或者发送事件
        // 暂时留空，后续在健康计划服务中实现
        
        // 7. 返回更新后的签到信息
        return convertToDTO(checkIn);
    }

    @Override
    public List<CheckInDTO> getCheckInRecords(Long studentId) {
        log.info("查询学生 {} 的签到记录", studentId);
        
        List<CheckIn> checkIns = checkInMapper.findByStudentId(studentId);
        log.info("学生 {} 共有 {} 条签到记录", studentId, checkIns.size());
        
        return checkIns.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CheckInDTO getCheckInById(Long checkInId) {
        log.info("查询签到记录详情: checkInId={}", checkInId);
        
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null) {
            log.warn("签到记录 {} 不存在", checkInId);
            throw new BusinessException(ResultCode.NOT_FOUND, "签到记录不存在");
        }
        
        return convertToDTO(checkIn);
    }

    @Override
    public List<CheckInDTO> getAllCheckIns(Long studentId, String status, String startDate, String endDate) {
        log.info("查询所有签到记录: studentId={}, status={}, startDate={}, endDate={}", studentId, status, startDate, endDate);
        
        List<CheckIn> checkIns = checkInMapper.findAll(studentId, status, startDate, endDate);
        log.info("共查询到 {} 条签到记录", checkIns.size());
        
        return checkIns.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public java.util.Map<String, Integer> getCheckInStatistics() {
        log.info("获取签到统计数据");
        
        // 获取所有签到记录
        List<CheckIn> allCheckIns = checkInMapper.selectList(null);
        int totalCheckIns = allCheckIns.size();
        
        // 统计已完成和未完成的签到
        int completedCheckIns = 0;
        int pendingCheckIns = 0;
        
        for (CheckIn checkIn : allCheckIns) {
            if (checkIn.getCheckOutTime() != null) {
                completedCheckIns++;
            } else {
                pendingCheckIns++;
            }
        }
        
        // 构建统计结果
        java.util.Map<String, Integer> statistics = new java.util.HashMap<>();
        statistics.put("totalCheckIns", totalCheckIns);
        statistics.put("completedCheckIns", completedCheckIns);
        statistics.put("pendingCheckIns", pendingCheckIns);
        
        log.info("签到统计数据: 总签到次数={}, 已完成签到={}, 未完成签到={}", totalCheckIns, completedCheckIns, pendingCheckIns);
        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CheckInDTO updateCheckInStatus(Long checkInId, CheckOutRequest request) {
        log.info("更新签到状态: checkInId={}, request={}", checkInId, request);
        
        // 1. 查询签到记录
        CheckIn checkIn = checkInMapper.selectById(checkInId);
        if (checkIn == null) {
            log.warn("签到记录 {} 不存在", checkInId);
            throw new BusinessException(ResultCode.NOT_FOUND, "签到记录不存在");
        }
        
        // 2. 更新签到状态
        if ("CHECKED_OUT".equals(request.getStatus())) {
            // 设置签退时间
            if (request.getCheckOutTime() != null) {
                checkIn.setCheckOutTime(request.getCheckOutTime());
            } else {
                checkIn.setCheckOutTime(LocalDateTime.now());
            }
            
            // 计算活动时长（分钟）
            if (checkIn.getCheckInTime() != null) {
                Duration duration = Duration.between(checkIn.getCheckInTime(), checkIn.getCheckOutTime());
                int durationMinutes = (int) duration.toMinutes();
                checkIn.setDurationMinutes(durationMinutes);
            }
            
            // 设置消耗卡路里
            if (request.getCalories() != null) {
                checkIn.setCaloriesBurned(BigDecimal.valueOf(request.getCalories()));
            } else if (checkIn.getDurationMinutes() != null) {
                // 简单估算：每分钟消耗5卡路里
                BigDecimal estimatedCalories = BigDecimal.valueOf(checkIn.getDurationMinutes() * 5);
                checkIn.setCaloriesBurned(estimatedCalories);
            }
        }
        
        checkInMapper.updateById(checkIn);
        log.info("签到状态更新成功 - 签到ID: {}", checkInId);
        
        // 3. 返回更新后的签到信息
        return convertToDTO(checkIn);
    }

    @Override
    public List<com.fitness.dto.ReservationDTO> getUncheckedInReservations(Long studentId, String startDate, String endDate) {
        log.info("查询未签到的预约记录: studentId={}, startDate={}, endDate={}", studentId, startDate, endDate);
        
        List<com.fitness.entity.Reservation> reservations = checkInMapper.findUncheckedInReservations(studentId, startDate, endDate);
        log.info("共查询到 {} 条未签到的预约记录", reservations.size());
        
        return reservations.stream()
                .map(this::convertReservationToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 将预约实体转换为DTO
     */
    private com.fitness.dto.ReservationDTO convertReservationToDTO(com.fitness.entity.Reservation reservation) {
        com.fitness.dto.ReservationDTO dto = com.fitness.dto.ReservationDTO.builder()
                .id(reservation.getId())
                .studentId(reservation.getStudentId())
                .reservationType(reservation.getReservationType())
                .courseId(reservation.getCourseId())
                .equipmentId(reservation.getEquipmentId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
        
        // 填充学生姓名
        User student = userMapper.selectById(reservation.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName() != null ? student.getRealName() : student.getUsername());
        }
        
        // 填充课程信息
        if (reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null) {
                dto.setCourseName(course.getName());
            }
        }
        
        return dto;
    }

    /**
     * 将签到实体转换为DTO
     */
    private CheckInDTO convertToDTO(CheckIn checkIn) {
        CheckInDTO dto = CheckInDTO.builder()
                .id(checkIn.getId())
                .reservationId(checkIn.getReservationId())
                .studentId(checkIn.getStudentId())
                .checkInTime(checkIn.getCheckInTime())
                .checkOutTime(checkIn.getCheckOutTime())
                .durationMinutes(checkIn.getDurationMinutes())
                .location(checkIn.getLocation())
                .caloriesBurned(checkIn.getCaloriesBurned())
                .createdAt(checkIn.getCreatedAt())
                .updatedAt(checkIn.getUpdatedAt())
                .build();
        
        // 填充学生姓名
        User student = userMapper.selectById(checkIn.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName() != null ? student.getRealName() : student.getUsername());
        }
        
        // 填充预约类型和课程信息
        Reservation reservation = reservationMapper.selectById(checkIn.getReservationId());
        if (reservation != null) {
            dto.setReservationType(reservation.getReservationType() != null ? reservation.getReservationType().name() : null);
            // 填充课程ID和课程名称
            if (reservation.getCourseId() != null) {
                dto.setCourseId(reservation.getCourseId());
                // 需要从课程表中查询课程名称
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    dto.setCourseName(course.getName());
                }
            }
        }
        
        // 填充状态
        dto.setStatus(checkIn.getCheckOutTime() != null ? "CHECKED_OUT" : "CHECKED_IN");
        
        // 填充时长和卡路里
        dto.setDuration(checkIn.getDurationMinutes());
        if (checkIn.getCaloriesBurned() != null) {
            dto.setCalories(checkIn.getCaloriesBurned().intValue());
        }
        
        return dto;
    }
}
