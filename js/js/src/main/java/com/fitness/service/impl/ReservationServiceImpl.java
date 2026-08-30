package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.common.ResultCode;
import com.fitness.dto.ReservationDTO;
import com.fitness.dto.ReserveCourseRequest;
import com.fitness.dto.ReserveEquipmentRequest;
import com.fitness.entity.*;
import com.fitness.entity.enums.EquipmentStatus;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.*;
import com.fitness.service.NotificationService;
import com.fitness.service.ReservationService;
import com.fitness.util.ConflictDetector;
import com.fitness.util.DistributedLockManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 预约服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationMapper reservationMapper;
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final ConflictDetector conflictDetector;
    private final NotificationService notificationService;
    private final DistributedLockManager distributedLockManager;
    private final EquipmentMapper equipmentMapper;
    private final CheckInMapper checkInMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReservationDTO reserveCourse(Long studentId, ReserveCourseRequest request) {
        log.info("学生 {} 预约课程 {}", studentId, request.getCourseId());
        
        Long courseId = request.getCourseId();
        
        // 1. 查询课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("课程 {} 不存在", courseId);
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }
        
        // 2. 检查课程容量限制
        if (!conflictDetector.isCourseAvailable(courseId)) {
            log.warn("课程 {} 已满员", courseId);
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程已满员，无法预约");
        }
        
        // 3. 检查学生时间冲突
        if (conflictDetector.hasTimeConflict(studentId, course.getStartTime(), course.getEndTime(),ReservationType.COURSE)) {
            log.warn("学生 {} 在时间段 {} 到 {} 存在时间冲突", 
                     studentId, course.getStartTime(), course.getEndTime());
            throw new BusinessException(ResultCode.BAD_REQUEST, "该时间段已有其他预约，存在时间冲突");
        }
        
        // 4. 检查学生是否已经预约过该课程
        List<Reservation> existingReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getStudentId, studentId)
                        .eq(Reservation::getCourseId, courseId)
                        .eq(Reservation::getReservationType, ReservationType.COURSE)
        );
        if (!existingReservations.isEmpty()) {
            log.warn("学生 {} 已经预约过课程 {}", studentId, courseId);
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已经预约过该课程，不能重复预约");
        }
        
        // 4. 创建预约记录
        Reservation reservation = Reservation.builder()
                .studentId(studentId)
                .reservationType(ReservationType.COURSE)
                .courseId(courseId)
                .startTime(course.getStartTime())
                .endTime(course.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();
        
        reservationMapper.insert(reservation);
        log.info("创建预约记录成功 - 预约ID: {}", reservation.getId());
        // 5. 更新课程报名人数
        course.setCurrentEnrollment(course.getCurrentEnrollment()+1);
        int updated = courseMapper.updateById(course);
        if (updated == 0) {
            log.error("更新课程 {} 报名人数失败，可能已满员", courseId);
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程已满员，预约失败");
        }
        log.info("课程 {} 报名人数已更新", courseId);
        
        // 6. 发送预约确认通知
        notificationService.sendReservationConfirmation(reservation);
        
        // 7. 返回预约信息
        return convertToDTO(reservation, course);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ReservationDTO reserveEquipment(Long studentId, ReserveEquipmentRequest request) {

        log.info("学生 {} 预约器材 {}, 时间段: {} 到 {}",
                studentId, request.getEquipmentId(), request.getStartTime(), request.getEndTime());

        Long equipmentId = request.getEquipmentId();

        // 1 查询器材
        Equipment equipment = equipmentMapper.selectById(equipmentId);
        if (equipment == null) {
            log.warn("器材 {} 不存在", equipmentId);
            throw new BusinessException(ResultCode.NOT_FOUND, "器材不存在");
        }

        // 2 检查器材是否可用
        if (!conflictDetector.isEquipmentAvailable(equipmentId,
                request.getStartTime(), request.getEndTime())) {

            log.warn("器材 {} 在时间段 {} 到 {} 已被预约",
                    equipmentId, request.getStartTime(), request.getEndTime());

            throw new BusinessException(ResultCode.BAD_REQUEST, "该器材在指定时间段已被预约");
        }

        // 3 查询时间冲突的预约
        List<Reservation> conflictReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                        .eq(Reservation::getStudentId, studentId)
                        .eq(Reservation::getReservationType, ReservationType.EQUIPMENT)
                        .ne(Reservation::getStatus, ReservationStatus.CANCELLED)
                        .lt(Reservation::getStartTime, request.getEndTime())
                        .gt(Reservation::getEndTime, request.getStartTime())
        );

        if (!conflictReservations.isEmpty()) {

            List<Long> reservationIds = conflictReservations.stream()
                    .map(Reservation::getId)
                    .toList();

            // 查询签到记录
            List<CheckIn> checkIns = checkInMapper.selectList(
                    new LambdaQueryWrapper<CheckIn>()
                            .in(CheckIn::getReservationId, reservationIds)
            );

            // 判断是否存在未签退
            boolean hasNotCheckout = checkIns.stream()
                    .anyMatch(c -> c.getCheckOutTime() == null);

            if (hasNotCheckout) {

                log.warn("学生 {} 在时间段 {} 到 {} 存在未签退预约冲突",
                        studentId, request.getStartTime(), request.getEndTime());

                throw new BusinessException(ResultCode.BAD_REQUEST,
                        "该时间段已有其他预约，存在时间冲突");
            }
        }

        // 4 创建预约
        Reservation reservation = Reservation.builder()
                .studentId(studentId)
                .reservationType(ReservationType.EQUIPMENT)
                .equipmentId(equipmentId)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .status(ReservationStatus.CONFIRMED)
                .build();

        reservationMapper.insert(reservation);

        log.info("创建器材预约记录成功 - 预约ID: {}", reservation.getId());

        // 5 更新器材状态
        equipment.setStatus(EquipmentStatus.IN_USE);
        equipmentMapper.updateById(equipment);

        log.info("器材 {} 状态已更新为使用中", equipmentId);

        // 6 通知
        notificationService.sendReservationConfirmation(reservation);

        // 7 返回
        return convertToDTO(reservation, equipment);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cancelReservation(Long reservationId, Long userId, String cancelReason) {
        log.info("用户 {} 取消预约 {}, 原因: {}", userId, reservationId, cancelReason);
        
        // 1. 查询预约记录
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            log.warn("预约 {} 不存在", reservationId);
            throw new BusinessException(ResultCode.RESERVATION_NOT_FOUND, "预约不存在");
        }
        
        // 2. 验证预约是否属于该用户或该用户是课程教练
        boolean isStudentOwner = reservation.getStudentId().equals(userId);
        boolean isCoachOwner = false;
        
        // 检查是否是课程教练
        if (reservation.getReservationType() == ReservationType.COURSE && reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            isCoachOwner = course != null && course.getCoachId().equals(userId);
        }
        
        if (!isStudentOwner && !isCoachOwner) {
            log.warn("用户 {} 无权取消预约 {}", userId, reservationId);
            throw new BusinessException(ResultCode.FORBIDDEN, "无权取消该预约");
        }
        
        // 3. 检查预约状态
        if (reservation.getStatus() == ReservationStatus.CANCELLED) {
            log.warn("预约 {} 已经被取消", reservationId);
            throw new BusinessException(ResultCode.BAD_REQUEST, "预约已被取消");
        }
        
        if (reservation.getStatus() == ReservationStatus.COMPLETED) {
            log.warn("预约 {} 已完成，无法取消", reservationId);
            throw new BusinessException(ResultCode.BAD_REQUEST, "预约已完成，无法取消");
        }
        
        // 4. 更新预约状态为已取消
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelReason(cancelReason);
        reservation.setCancelledAt(java.time.LocalDateTime.now());
        reservationMapper.updateById(reservation);
        log.info("预约 {} 状态已更新为已取消", reservationId);
        
        // 5. 如果是课程预约，释放课程名额
        if (reservation.getReservationType() == ReservationType.COURSE && reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            course.setCurrentEnrollment(course.getCurrentEnrollment()-1);
            int updated = courseMapper.updateById(course);
            if (updated > 0) {
                log.info("课程 {} 报名人数已减少", reservation.getCourseId());
            } else {
                log.warn("课程 {} 报名人数减少失败", reservation.getCourseId());
            }
        }
        
        // 如果是器材预约，释放器材
        if (reservation.getReservationType() == ReservationType.EQUIPMENT && reservation.getEquipmentId() != null) {
            Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus(EquipmentStatus.AVAILABLE);
                int updated = equipmentMapper.updateById(equipment);
                if (updated > 0) {
                    log.info("器材 {} 状态已更新为可用", reservation.getEquipmentId());
                } else {
                    log.warn("器材 {} 状态更新失败", reservation.getEquipmentId());
                }
            } else {
                log.warn("器材 {} 不存在，无法更新状态", reservation.getEquipmentId());
            }
        }
        
        // 6. 检查取消时间并记录迟到取消次数
        // 如果取消时间距离课程开始少于2小时，记录迟到取消次数
        java.time.LocalDateTime now = java.time.LocalDateTime.now();
        java.time.LocalDateTime startTime = reservation.getStartTime();
        long hoursUntilStart = java.time.Duration.between(now, startTime).toHours();
        
        if (hoursUntilStart < 2 && hoursUntilStart >= 0) {
            log.info("预约 {} 为迟到取消（距离开始时间 {} 小时），记录迟到取消次数", reservationId, hoursUntilStart);
            int incrementResult = studentProfileMapper.incrementLateCancellationCount(userId);
            if (incrementResult > 0) {
                log.info("学生 {} 的迟到取消次数已增加", userId);
            } else {
                log.warn("学生 {} 的迟到取消次数增加失败", userId);
            }
        }
        
        // 7. 发送取消通知
        notificationService.sendCancellationNotice(reservation);
        
        log.info("预约 {} 取消成功", reservationId);
    }

    @Override
    public ReservationDTO getReservationById(Long reservationId) {
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "预约不存在");
        }
        
        // 根据预约类型查询关联信息
        if (reservation.getReservationType() == ReservationType.COURSE) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            return convertToDTO(reservation, course);
        } else if (reservation.getReservationType() == ReservationType.EQUIPMENT) {
            Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            return convertToDTO(reservation, equipment);
        } else {
            return convertToDTO(reservation, (Course) null);
        }
    }

    /**
     * 将预约实体转换为DTO
     */
    private ReservationDTO convertToDTO(Reservation reservation, Course course) {
        ReservationDTO dto = ReservationDTO.builder()
                .id(reservation.getId())
                .studentId(reservation.getStudentId())
                .reservationType(reservation.getReservationType())
                .courseId(reservation.getCourseId())
                .equipmentId(reservation.getEquipmentId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
        
        // 填充课程名称和时间表
        if (course != null) {
            dto.setCourseName(course.getName());
            // 填充教练姓名
            User coach = userMapper.selectById(course.getCoachId());
            if (coach != null) {
                dto.setCoachName(coach.getRealName());
            }
            // 填充课程时间表
            if (course.getClassSchedule() != null && !course.getClassSchedule().isEmpty()) {
                java.util.List<com.fitness.dto.ScheduleItemDTO> scheduleItems = new java.util.ArrayList<>();
                for (com.fitness.entity.Course.ScheduleItem item : course.getClassSchedule()) {
                    com.fitness.dto.ScheduleItemDTO scheduleItemDTO = com.fitness.dto.ScheduleItemDTO.builder()
                            .dayOfWeek(item.getDayOfWeek())
                            .startTime(item.getStartTime())
                            .endTime(item.getEndTime())
                            .build();
                    scheduleItems.add(scheduleItemDTO);
                }
                dto.setCourseSchedule(scheduleItems);
            }
        }
        
        // 填充学生姓名
        User student = userMapper.selectById(reservation.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName());
        }
        
        return dto;
    }

    /**
     * 将预约实体转换为DTO（器材预约）
     */
    private ReservationDTO convertToDTO(Reservation reservation, Equipment equipment) {
        ReservationDTO dto = ReservationDTO.builder()
                .id(reservation.getId())
                .studentId(reservation.getStudentId())
                .reservationType(reservation.getReservationType())
                .courseId(reservation.getCourseId())
                .equipmentId(reservation.getEquipmentId())
                .startTime(reservation.getStartTime())
                .endTime(reservation.getEndTime())
                .status(reservation.getStatus())
                .cancelReason(reservation.getCancelReason())
                .cancelledAt(reservation.getCancelledAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();
        
        // 填充器材名称
        if (equipment != null) {
            dto.setEquipmentName(equipment.getName());
        }
        
        // 填充学生姓名
        User student = userMapper.selectById(reservation.getStudentId());
        if (student != null) {
            dto.setStudentName(student.getRealName());
        }
        
        return dto;
    }


    @Override
    public java.util.List<ReservationDTO> getReservations(Long studentId,
                                                           com.fitness.entity.enums.ReservationType reservationType,
                                                           com.fitness.entity.enums.ReservationStatus status,
                                                           String keyword) {
        log.info("查询预约列表 - 学生ID: {}, 类型: {}, 状态: {}, 关键词: {}", studentId, reservationType, status, keyword);

        com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Reservation> queryWrapper =
            new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<>();

        // 如果指定了学生ID，则只查询该学生的预约
        if (studentId != null) {
            queryWrapper.eq(Reservation::getStudentId, studentId);
        }

        // 如果指定了预约类型，则按类型筛选
        if (reservationType != null) {
            queryWrapper.eq(Reservation::getReservationType, reservationType);
        }

        // 如果指定了状态，则按状态筛选
        if (status != null) {
            queryWrapper.eq(Reservation::getStatus, status);
        }

        // 按开始时间倒序排列
        queryWrapper.orderByDesc(Reservation::getStartTime);

        java.util.List<Reservation> reservations = reservationMapper.selectList(queryWrapper);
        log.info("查询到 {} 条预约记录", reservations.size());

        // 转换为DTO并应用关键词过滤
        return reservations.stream()
                .map(reservation -> {
                    if (reservation.getReservationType() == ReservationType.COURSE) {
                        Course course = courseMapper.selectById(reservation.getCourseId());
                        return convertToDTO(reservation, course);
                    } else if (reservation.getReservationType() == ReservationType.EQUIPMENT) {
                        Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
                        return convertToDTO(reservation, equipment);
                    } else {
                        return convertToDTO(reservation, (Course) null);
                    }
                })
                .filter(dto -> {
                    if (keyword == null || keyword.trim().isEmpty()) {
                        return true;
                    }
                    String lowerKeyword = keyword.toLowerCase();
                    // 检查学生姓名
                    User student = userMapper.selectById(dto.getStudentId());
                    if (student != null) {
                        if (student.getRealName() != null && student.getRealName().toLowerCase().contains(lowerKeyword)) {
                            return true;
                        }
                    }
                    // 检查课程名称
                    if (dto.getCourseName() != null && dto.getCourseName().toLowerCase().contains(lowerKeyword)) {
                        return true;
                    }
                    // 检查器材名称
                    if (dto.getEquipmentName() != null && dto.getEquipmentName().toLowerCase().contains(lowerKeyword)) {
                        return true;
                    }
                    return false;
                })
                .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<ReservationDTO> getCoachCourseReservations(Long coachId, ReservationType reservationType, ReservationStatus status) {
        log.info("查询教练 {} 的课程预约列表 - 类型: {}, 状态: {}", coachId, reservationType, status);

        // 1. 查询教练的所有课程
        List<Course> courses = courseMapper.selectList(
            new LambdaQueryWrapper<Course>()
                .eq(Course::getCoachId, coachId)
        );

        if (courses.isEmpty()) {
            log.info("教练 {} 没有课程，返回空列表", coachId);
            return new ArrayList<>();
        }

        // 2. 获取课程ID列表
        List<Long> courseIds = courses.stream()
            .map(Course::getId)
            .collect(Collectors.toList());

        // 3. 查询这些课程的预约
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        // 只查询课程预约
        queryWrapper.eq(Reservation::getReservationType, ReservationType.COURSE);
        // 只查询教练课程的预约
        queryWrapper.in(Reservation::getCourseId, courseIds);

        // 4. 按状态筛选
        if (status != null) {
            queryWrapper.eq(Reservation::getStatus, status);
        }

        // 5. 按开始时间倒序排列
        queryWrapper.orderByDesc(Reservation::getStartTime);

        List<Reservation> reservations = reservationMapper.selectList(queryWrapper);
        log.info("查询到 {} 条预约记录", reservations.size());

        if (reservations.isEmpty()) {
            return new ArrayList<>();
        }

        // 6. 批量查询学生信息
        List<Long> studentIds = reservations.stream()
            .map(Reservation::getStudentId)
            .distinct()
            .collect(Collectors.toList());
        Map<Long, User> studentMap = studentIds.isEmpty()
            ? new HashMap<>()
            : userMapper.selectBatchIds(studentIds)
            .stream()
            .collect(Collectors.toMap(User::getId, u -> u));

        // 7. 转换为DTO
        return reservations.stream()
            .map(reservation -> {
                Course course = courseMapper.selectById(reservation.getCourseId());
                ReservationDTO dto = convertToDTO(reservation, course);
                // 填充学生姓名
                User studentInfo = studentMap.get(reservation.getStudentId());
                if (studentInfo != null) {
                    dto.setStudentName(studentInfo.getRealName());
                }
                return dto;
            })
            .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO confirmReservation(Long reservationId, Long userId) {
        log.info("确认预约: reservationId={}, userId={}", reservationId, userId);

        // 1. 查询预约记录
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            log.warn("预约 {} 不存在", reservationId);
            throw new BusinessException(ResultCode.NOT_FOUND, "预约不存在");
        }

        // 2. 检查预约状态
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("预约 {} 状态不是待确认，当前状态: {}", reservationId, reservation.getStatus());
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能确认待确认状态的预约");
        }

        // 3. 检查权限（教练只能确认自己课程的预约）
        if (reservation.getReservationType() == ReservationType.COURSE && reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null && !course.getCoachId().equals(userId)) {
                log.warn("用户 {} 无权确认课程 {} 的预约", userId, course.getId());
                throw new BusinessException(ResultCode.FORBIDDEN, "无权确认此预约");
            }
        }

        // 4. 更新预约状态为已确认
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationMapper.updateById(reservation);
        log.info("预约 {} 状态已更新为已确认", reservationId);

        // 5. 发送确认通知
        notificationService.sendReservationConfirmation(reservation);

        // 6. 返回更新后的预约信息
        if (reservation.getReservationType() == ReservationType.COURSE) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            return convertToDTO(reservation, course);
        } else if (reservation.getReservationType() == ReservationType.EQUIPMENT) {
            Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            return convertToDTO(reservation, equipment);
        } else {
            return convertToDTO(reservation, (Course) null);
        }
    }

    @Override
    public ReservationDTO rejectReservation(Long reservationId, Long userId, String rejectReason) {
        log.info("拒绝预约: reservationId={}, userId={}, reason={}", reservationId, userId, rejectReason);

        // 1. 查询预约记录
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            log.warn("预约 {} 不存在", reservationId);
            throw new BusinessException(ResultCode.NOT_FOUND, "预约不存在");
        }

        // 2. 检查预约状态
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            log.warn("预约 {} 状态不是待确认，当前状态: {}", reservationId, reservation.getStatus());
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能拒绝待确认状态的预约");
        }

        // 3. 检查权限（教练只能拒绝自己课程的预约）
        if (reservation.getReservationType() == ReservationType.COURSE && reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null && !course.getCoachId().equals(userId)) {
                log.warn("用户 {} 无权拒绝课程 {} 的预约", userId, course.getId());
                throw new BusinessException(ResultCode.FORBIDDEN, "无权拒绝此预约");
            }
        }

        // 4. 更新预约状态为已取消
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelReason(rejectReason);
        reservation.setCancelledAt(java.time.LocalDateTime.now());
        reservationMapper.updateById(reservation);
        log.info("预约 {} 状态已更新为已取消", reservationId);

        // 5. 如果是课程预约，释放课程名额
        if (reservation.getReservationType() == ReservationType.COURSE && reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null) {
                course.setCurrentEnrollment(course.getCurrentEnrollment() - 1);
                courseMapper.updateById(course);
                log.info("课程 {} 报名人数已减少", course.getId());
            }
        }

        // 6. 发送拒绝通知
        notificationService.sendCancellationNotice(reservation);

        // 7. 返回更新后的预约信息
        if (reservation.getReservationType() == ReservationType.COURSE) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            return convertToDTO(reservation, course);
        } else if (reservation.getReservationType() == ReservationType.EQUIPMENT) {
            Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            return convertToDTO(reservation, equipment);
        } else {
            return convertToDTO(reservation, (Course) null);
        }
    }

    @Override
    public ReservationDTO completeReservation(Long reservationId, Long userId) {
        log.info("完成预约: reservationId={}, userId={}", reservationId, userId);

        // 1. 查询预约记录
        Reservation reservation = reservationMapper.selectById(reservationId);
        if (reservation == null) {
            log.warn("预约 {} 不存在", reservationId);
            throw new BusinessException(ResultCode.NOT_FOUND, "预约不存在");
        }

        // 2. 检查预约状态
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            log.warn("预约 {} 状态不是已确认，当前状态: {}", reservationId, reservation.getStatus());
            throw new BusinessException(ResultCode.BAD_REQUEST, "只能完成已确认状态的预约");
        }

        // 3. 检查权限（教练只能完成自己课程的预约）
        if (reservation.getReservationType() == ReservationType.COURSE && reservation.getCourseId() != null) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            if (course != null && !course.getCoachId().equals(userId)) {
                log.warn("用户 {} 无权完成课程 {} 的预约", userId, course.getId());
                throw new BusinessException(ResultCode.FORBIDDEN, "无权完成此预约");
            }
        }

        // 4. 更新预约状态为已完成
        reservation.setStatus(ReservationStatus.COMPLETED);
        reservationMapper.updateById(reservation);
        log.info("预约 {} 状态已更新为已完成", reservationId);

        // 5. 如果是器材预约，释放器材
        if (reservation.getReservationType() == ReservationType.EQUIPMENT && reservation.getEquipmentId() != null) {
            Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            if (equipment != null) {
                equipment.setStatus(EquipmentStatus.AVAILABLE);
                equipmentMapper.updateById(equipment);
                log.info("器材 {} 状态已更新为可用", equipment.getId());
            }
        }

        // 6. 发送完成通知
        notificationService.sendReservationCompletionNotice(reservation);

        // 7. 返回更新后的预约信息
        if (reservation.getReservationType() == ReservationType.COURSE) {
            Course course = courseMapper.selectById(reservation.getCourseId());
            return convertToDTO(reservation, course);
        } else if (reservation.getReservationType() == ReservationType.EQUIPMENT) {
            Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
            return convertToDTO(reservation, equipment);
        } else {
            return convertToDTO(reservation, (Course) null);
        }
    }

    @Override
    public List<ReservationDTO> getMyReservations(Reservation reservation) {

        log.info("查询学生 {} 的预约列表", reservation.getStudentId());

        List<Reservation> reservations =
                reservationMapper.findByStudentId(
                        reservation.getStudentId(),
                        reservation.getReservationType());

        if (reservations.isEmpty()) {
            return Collections.emptyList();
        }

        // 1 获取 reservationId
        List<Long> reservationIds = reservations.stream()
                .map(Reservation::getId)
                .toList();

        // 2 获取 courseId
        List<Long> courseIds = reservations.stream()
                .filter(r -> r.getCourseId() != null)
                .map(Reservation::getCourseId)
                .distinct()
                .toList();

        // 3 获取 equipmentId
        List<Long> equipmentIds = reservations.stream()
                .filter(r -> r.getEquipmentId() != null)
                .map(Reservation::getEquipmentId)
                .distinct()
                .toList();

        // 4 批量查询签到
        Map<Long, CheckIn> checkInMap = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .in(CheckIn::getReservationId, reservationIds)
        ).stream().collect(Collectors.toMap(
                CheckIn::getReservationId,
                c -> c,
                (a, b) -> a
        ));

        // 5 批量查询课程
        Map<Long, Course> courseMap = courseIds.isEmpty()
                ? new HashMap<>()
                : courseMapper.selectBatchIds(courseIds)
                .stream()
                .collect(Collectors.toMap(Course::getId, c -> c));

        // 6 批量查询器材
        Map<Long, Equipment> equipmentMap = equipmentIds.isEmpty()
                ? new HashMap<>()
                : equipmentMapper.selectBatchIds(equipmentIds)
                .stream()
                .collect(Collectors.toMap(Equipment::getId, e -> e));

        // 1 获取教练ID列表
        List<Long> coachIds = courseMap.values().stream()
                .map(Course::getCoachId)
                .distinct()
                .toList();

        // 2 批量查询教练信息
        Map<Long, User> coachMap = coachIds.isEmpty()
                ? new HashMap<>()
                : userMapper.selectBatchIds(coachIds)
                .stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 3 批量查询学生信息
        Map<Long, User> studentMap = new HashMap<>();
        User student = userMapper.selectById(reservation.getStudentId());
        if (student != null) {
            studentMap.put(student.getId(), student);
        }

        // 7 组装DTO
        return reservations.stream().map(res -> {

            ReservationDTO dto = ReservationDTO.builder()
                    .id(res.getId())
                    .studentId(res.getStudentId())
                    .reservationType(res.getReservationType())
                    .courseId(res.getCourseId())
                    .equipmentId(res.getEquipmentId())
                    .startTime(res.getStartTime())
                    .endTime(res.getEndTime())
                    .status(res.getStatus())
                    .cancelReason(res.getCancelReason())
                    .cancelledAt(res.getCancelledAt())
                    .createdAt(res.getCreatedAt())
                    .updatedAt(res.getUpdatedAt())
                    .build();

            // 课程信息
            if (res.getCourseId() != null) {
                Course course = courseMap.get(res.getCourseId());
                if (course != null) {
                    dto.setCourseName(course.getName());
                    dto.setLocation(course.getLocation());
                    // 填充教练姓名
                    User coach = coachMap.get(course.getCoachId());
                    if (coach != null) {
                        dto.setCoachName(coach.getRealName());
                    }
                    // 填充课程时间表
                    if (course.getClassSchedule() != null && !course.getClassSchedule().isEmpty()) {
                        java.util.List<com.fitness.dto.ScheduleItemDTO> scheduleItems = new java.util.ArrayList<>();
                        for (com.fitness.entity.Course.ScheduleItem item : course.getClassSchedule()) {
                            com.fitness.dto.ScheduleItemDTO scheduleItemDTO = com.fitness.dto.ScheduleItemDTO.builder()
                                    .dayOfWeek(item.getDayOfWeek())
                                    .startTime(item.getStartTime())
                                    .endTime(item.getEndTime())
                                    .build();
                            scheduleItems.add(scheduleItemDTO);
                        }
                        dto.setCourseSchedule(scheduleItems);
                    }
                }
            }

            // 器材信息
            if (res.getEquipmentId() != null) {
                Equipment equipment = equipmentMap.get(res.getEquipmentId());
                if (equipment != null) {
                    dto.setEquipmentName(equipment.getName());
                    dto.setLocation(equipment.getLocation());
                }
            }

            // 填充学生姓名
            User studentInfo = studentMap.get(res.getStudentId());
            if (studentInfo != null) {
                dto.setStudentName(studentInfo.getRealName());
            }

            // 签到信息
            CheckIn checkIn = checkInMap.get(res.getId());

            if (checkIn != null) {
                dto.setCheckedIn(checkIn.getCheckInTime() != null);
                dto.setCheckedOut(checkIn.getCheckOutTime() != null);
            } else {
                dto.setCheckedIn(false);
                dto.setCheckedOut(false);
            }

            return dto;

        }).toList();
    }
    @Override
    public List<Reservation> getStudentsByCourseId(Long courseId) {

        LambdaQueryWrapper<Reservation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Reservation::getCourseId, courseId)
                .eq(Reservation::getReservationType, ReservationType.COURSE);

        List<Reservation> reservations = reservationMapper.selectList(wrapper);

        if (reservations.isEmpty()) {
            return reservations;
        }

        // 1 获取所有 studentId
        List<Long> studentIds = reservations.stream()
                .map(Reservation::getStudentId)
                .distinct()
                .toList();

        // 2 查询学生
        List<User> users = userMapper.selectBatchIds(studentIds);

        // 3 转Map
        Map<Long, User> userMap = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));

        // 4 组装
        for (Reservation reservation : reservations) {
            reservation.setUser(userMap.get(reservation.getStudentId()));
        }

        return reservations;
    }

    @Override
    public com.fitness.common.Result<?> getStudentReservations(Long studentId, Integer page, Integer pageSize, ReservationType type, ReservationStatus status) {
        log.info("学生 {} 获取预约列表 - 页码: {}, 每页大小: {}, 类型: {}, 状态: {}", studentId, page, pageSize, type, status);

        // 构建查询条件
        LambdaQueryWrapper<Reservation> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Reservation::getStudentId, studentId);

        // 按类型筛选
        if (type != null) {
            queryWrapper.eq(Reservation::getReservationType, type);
        }

        // 按状态筛选
        if (status != null) {
            queryWrapper.eq(Reservation::getStatus, status);
        }

        // 按开始时间倒序排列
        queryWrapper.orderByDesc(Reservation::getStartTime);

        // 计算偏移量
        int offset = (page - 1) * pageSize;

        // 查询总数
        long total = reservationMapper.selectCount(queryWrapper);

        // 查询分页数据
        List<Reservation> reservations = reservationMapper.selectList(queryWrapper
                .last("LIMIT " + offset + ", " + pageSize));

        log.info("查询到 {} 条预约记录，总数: {}", reservations.size(), total);

        // 转换为DTO
        List<ReservationDTO> dtos = reservations.stream()
                .map(reservation -> {
                    if (reservation.getReservationType() == ReservationType.COURSE) {
                        Course course = courseMapper.selectById(reservation.getCourseId());
                        return convertToDTO(reservation, course);
                    } else if (reservation.getReservationType() == ReservationType.EQUIPMENT) {
                        Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
                        return convertToDTO(reservation, equipment);
                    } else {
                        return convertToDTO(reservation, (Course) null);
                    }
                })
                .collect(Collectors.toList());

        // 构建分页响应
        Map<String, Object> result = new HashMap<>();
        result.put("records", dtos);
        result.put("total", total);
        result.put("current", page);
        result.put("size", pageSize);
        result.put("pages", (total + pageSize - 1) / pageSize);

        return com.fitness.common.Result.success(result);
    }
}



