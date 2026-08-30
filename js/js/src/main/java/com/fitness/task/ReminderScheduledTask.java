package com.fitness.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.entity.Reservation;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.mapper.ReservationMapper;
import com.fitness.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 预约提醒定时任务
 * 定时扫描预约记录并发送提醒通知
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class ReminderScheduledTask {
    
    private final ReservationMapper reservationMapper;
    private final NotificationService notificationService;
    
    /**
     * 每10分钟执行一次，检查需要发送提醒的预约
     */
    @Scheduled(cron = "0 */10 * * * ?")
    public void sendCourseReminders() {
        try {
            log.debug("开始执行课程预约提醒任务");
            
            // 查找24小时内开始的课程预约（未取消、未完成）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderTime = now.plusHours(24);
            
            List<Reservation> courseReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                    .eq(Reservation::getReservationType, ReservationType.COURSE)
                    .in(Reservation::getStatus, ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
                    .ge(Reservation::getStartTime, now)
                    .le(Reservation::getStartTime, reminderTime)
            );
            
            for (Reservation reservation : courseReservations) {
                // 检查是否已经发送过提醒（可以通过添加字段或使用Redis记录）
                // 这里简化处理，每次都发送
                notificationService.sendReminder(reservation);
            }
            
            log.info("课程预约提醒任务完成，发送了 {} 条提醒", courseReservations.size());
            
        } catch (Exception e) {
            log.error("执行课程预约提醒任务失败", e);
        }
    }
    
    /**
     * 每5分钟执行一次，检查需要发送提醒的器材预约
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void sendEquipmentReminders() {
        try {
            log.debug("开始执行器材预约提醒任务");
            
            // 查找30分钟内开始的器材预约（未取消、未完成）
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reminderTime = now.plusMinutes(30);
            
            List<Reservation> equipmentReservations = reservationMapper.selectList(
                new LambdaQueryWrapper<Reservation>()
                    .eq(Reservation::getReservationType, ReservationType.EQUIPMENT)
                    .in(Reservation::getStatus, ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
                    .ge(Reservation::getStartTime, now)
                    .le(Reservation::getStartTime, reminderTime)
            );
            
            for (Reservation reservation : equipmentReservations) {
                // 检查是否已经发送过提醒（可以通过添加字段或使用Redis记录）
                // 这里简化处理，每次都发送
                notificationService.sendReminder(reservation);
            }
            
            log.info("器材预约提醒任务完成，发送了 {} 条提醒", equipmentReservations.size());
            
        } catch (Exception e) {
            log.error("执行器材预约提醒任务失败", e);
        }
    }
}
