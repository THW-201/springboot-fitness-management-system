package com.fitness.service;

import com.fitness.entity.HealthPlan;
import com.fitness.entity.Reservation;

/**
 * 通知服务接口
 * 提供预约确认、提醒等通知功能
 */
public interface NotificationService {

    /**
     * 发送预约确认通知
     * 
     * @param reservation 预约信息
     */
    void sendReservationConfirmation(Reservation reservation);

    /**
     * 发送预约提醒通知
     * 
     * @param reservation 预约信息
     */
    void sendReminder(Reservation reservation);

    /**
     * 发送取消通知
     * 
     * @param reservation 预约信息
     */
    void sendCancellationNotice(Reservation reservation);
    
    /**
     * 发送目标达成祝贺通知
     * 
     * @param healthPlan 健康计划
     */
    void sendGoalAchievement(HealthPlan healthPlan);
    
    /**
     * 发送预约完成通知
     * 
     * @param reservation 预约信息
     */
    void sendReservationCompletionNotice(Reservation reservation);
}
