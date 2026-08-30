package com.fitness.util;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.entity.Course;
import com.fitness.entity.Reservation;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.mapper.CourseMapper;
import com.fitness.mapper.ReservationMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约冲突检测器
 * 用于检测学生时间冲突、课程容量和器材可用性
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ConflictDetector {

    private final ReservationMapper reservationMapper;
    private final CourseMapper courseMapper;

    /**
     * 检测学生在指定时间段是否有时间冲突
     * 
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true表示有冲突，false表示无冲突
     */
    public boolean hasTimeConflict(Long studentId, LocalDateTime startTime, LocalDateTime endTime, ReservationType reservationType) {
        return hasTimeConflict(studentId, startTime, endTime, null,reservationType);
    }

    /**
     * 检测学生在指定时间段是否有时间冲突（可排除指定预约）
     * 
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeReservationId 排除的预约ID（用于更新预约时排除自己）
     * @return true表示有冲突，false表示无冲突
     */
    public boolean hasTimeConflict(Long studentId, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId, ReservationType reservationType) {
        log.debug("检测学生 {} 在时间段 {} 到 {} 的时间冲突，排除预约ID: {}", 
                  studentId, startTime, endTime, excludeReservationId);
        
        List<Reservation> conflictingReservations = reservationMapper.findConflictingReservations(
            studentId, startTime, endTime, excludeReservationId,reservationType
        );
        
        boolean hasConflict = !conflictingReservations.isEmpty();
        if (hasConflict) {
            log.info("学生 {} 在时间段 {} 到 {} 存在 {} 个时间冲突", 
                     studentId, startTime, endTime, conflictingReservations.size());
        }
        
        return hasConflict;
    }

    /**
     * 检测课程是否有可用容量
     * 
     * @param courseId 课程ID
     * @return true表示有可用容量，false表示已满
     */
    public boolean isCourseAvailable(Long courseId) {
        log.debug("检测课程 {} 的可用容量", courseId);
        
        // 查询课程信息
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("课程 {} 不存在", courseId);
            return false;
        }
        
        // 检查当前报名人数是否小于容量
        boolean isAvailable = course.getCurrentEnrollment() < course.getCapacity();
        
        if (!isAvailable) {
            log.info("课程 {} 已满员，当前报名人数: {}，容量: {}", 
                     courseId, course.getCurrentEnrollment(), course.getCapacity());
        }
        
        return isAvailable;
    }

    /**
     * 检测器材在指定时间段是否可用
     * 
     * @param equipmentId 器材ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return true表示可用，false表示已被预约
     */
    public boolean isEquipmentAvailable(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        return isEquipmentAvailable(equipmentId, startTime, endTime, null);
    }

    /**
     * 检测器材在指定时间段是否可用（可排除指定预约）
     * 
     * @param equipmentId 器材ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @param excludeReservationId 排除的预约ID（用于更新预约时排除自己）
     * @return true表示可用，false表示已被预约
     */
    public boolean isEquipmentAvailable(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime, Long excludeReservationId) {
        log.debug("检测器材 {} 在时间段 {} 到 {} 的可用性，排除预约ID: {}", 
                  equipmentId, startTime, endTime, excludeReservationId);
        
        List<Reservation> equipmentConflicts = reservationMapper.findEquipmentConflicts(
            equipmentId, startTime, endTime, excludeReservationId
        );
        
        boolean isAvailable = equipmentConflicts.isEmpty();
        if (!isAvailable) {
            log.info("器材 {} 在时间段 {} 到 {} 已被预约，存在 {} 个冲突", 
                     equipmentId, startTime, endTime, equipmentConflicts.size());
        }
        
        return isAvailable;
    }

//    /**
//     * 获取学生在指定时间段的冲突预约列表
//     *
//     * @param studentId 学生ID
//     * @param startTime 开始时间
//     * @param endTime 结束时间
//     * @return 冲突的预约列表
//     */
//    public List<Reservation> getConflictingReservations(Long studentId, LocalDateTime startTime, LocalDateTime endTime) {
//        return reservationMapper.findConflictingReservations(studentId, startTime, endTime, null);
//    }

    /**
     * 获取器材在指定时间段的冲突预约列表
     * 
     * @param equipmentId 器材ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 冲突的预约列表
     */
    public List<Reservation> getEquipmentConflicts(Long equipmentId, LocalDateTime startTime, LocalDateTime endTime) {
        return reservationMapper.findEquipmentConflicts(equipmentId, startTime, endTime, null);
    }

    /**
     * 获取课程当前报名人数
     * 
     * @param courseId 课程ID
     * @return 当前报名人数，如果课程不存在返回-1
     */
    public int getCurrentEnrollment(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("课程 {} 不存在", courseId);
            return -1;
        }
        return course.getCurrentEnrollment();
    }

    /**
     * 获取课程容量
     * 
     * @param courseId 课程ID
     * @return 课程容量，如果课程不存在返回-1
     */
    public int getCourseCapacity(Long courseId) {
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            log.warn("课程 {} 不存在", courseId);
            return -1;
        }
        return course.getCapacity();
    }
}
