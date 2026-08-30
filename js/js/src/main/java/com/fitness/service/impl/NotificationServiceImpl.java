package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.entity.*;
import com.fitness.entity.enums.ReservationType;
import com.fitness.mapper.CourseMapper;
import com.fitness.mapper.EquipmentMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

/**
 * 通知服务实现
 * 使用日志作为占位符实现，可以后续替换为邮件、短信或站内消息
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    
    private final UserMapper userMapper;
    private final CourseMapper courseMapper;
    private final EquipmentMapper equipmentMapper;
    
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    @Override
    public void sendReservationConfirmation(Reservation reservation) {
        try {
            User student = userMapper.selectById(reservation.getStudentId());
            if (student == null) {
                log.warn("学生不存在，无法发送预约确认通知: {}", reservation.getStudentId());
                return;
            }
            
            String message;
            if (reservation.getReservationType() == ReservationType.COURSE) {
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    message = String.format(
                        "【预约确认】尊敬的 %s，您已成功预约课程《%s》，时间：%s。请准时参加！",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        course.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约确认通知（课程信息未找到）";
                }
            } else {
                Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
                if (equipment != null) {
                    message = String.format(
                        "【预约确认】尊敬的 %s，您已成功预约器材《%s》，时间：%s - %s。请准时使用！",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        equipment.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER),
                        reservation.getEndTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约确认通知（器材信息未找到）";
                }
            }
            
            // 占位符实现：使用日志记录
            log.info("发送预约确认通知 - 用户: {}, 邮箱: {}, 消息: {}", 
                student.getUsername(), student.getEmail(), message);
            
            // TODO: 实际实现可以调用邮件服务、短信服务或站内消息服务
            // emailService.sendEmail(student.getEmail(), "预约确认", message);
            // smsService.sendSms(student.getPhone(), message);
            // inAppMessageService.sendMessage(student.getId(), message);
            
        } catch (Exception e) {
            log.error("发送预约确认通知失败", e);
        }
    }
    
    @Override
    public void sendReminder(Reservation reservation) {
        try {
            User student = userMapper.selectById(reservation.getStudentId());
            if (student == null) {
                log.warn("学生不存在，无法发送提醒通知: {}", reservation.getStudentId());
                return;
            }
            
            String message;
            if (reservation.getReservationType() == ReservationType.COURSE) {
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    message = String.format(
                        "【预约提醒】尊敬的 %s，您预约的课程《%s》将在24小时后开始（%s），请做好准备！",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        course.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约提醒通知（课程信息未找到）";
                }
            } else {
                Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
                if (equipment != null) {
                    message = String.format(
                        "【预约提醒】尊敬的 %s，您预约的器材《%s》将在30分钟后开始使用（%s），请准时到场！",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        equipment.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约提醒通知（器材信息未找到）";
                }
            }
            
            // 占位符实现：使用日志记录
            log.info("发送预约提醒通知 - 用户: {}, 邮箱: {}, 消息: {}", 
                student.getUsername(), student.getEmail(), message);
            
            // TODO: 实际实现可以调用邮件服务、短信服务或站内消息服务
            
        } catch (Exception e) {
            log.error("发送预约提醒通知失败", e);
        }
    }
    
    @Override
    public void sendCancellationNotice(Reservation reservation) {
        try {
            User student = userMapper.selectById(reservation.getStudentId());
            if (student == null) {
                log.warn("学生不存在，无法发送取消通知: {}", reservation.getStudentId());
                return;
            }
            
            String message;
            if (reservation.getReservationType() == ReservationType.COURSE) {
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    message = String.format(
                        "【预约取消】尊敬的 %s，您的课程预约《%s》（%s）已成功取消。",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        course.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约取消通知（课程信息未找到）";
                }
            } else {
                Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
                if (equipment != null) {
                    message = String.format(
                        "【预约取消】尊敬的 %s，您的器材预约《%s》（%s - %s）已成功取消。",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        equipment.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER),
                        reservation.getEndTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约取消通知（器材信息未找到）";
                }
            }
            
            // 占位符实现：使用日志记录
            log.info("发送预约取消通知 - 用户: {}, 邮箱: {}, 消息: {}", 
                student.getUsername(), student.getEmail(), message);
            
            // TODO: 实际实现可以调用邮件服务、短信服务或站内消息服务
            
        } catch (Exception e) {
            log.error("发送预约取消通知失败", e);
        }
    }
    
    @Override
    public void sendGoalAchievement(HealthPlan healthPlan) {
        try {
            User student = userMapper.selectById(healthPlan.getStudentId());
            if (student == null) {
                log.warn("学生不存在，无法发送目标达成通知: {}", healthPlan.getStudentId());
                return;
            }
            
            String message = String.format(
                "【目标达成】恭喜 %s！您的健康计划《%s》已达成目标，完成度：%.1f%%。继续保持，再创佳绩！",
                student.getRealName() != null ? student.getRealName() : student.getUsername(),
                healthPlan.getPlanName(),
                healthPlan.getCompletionPercentage()
            );
            
            // 占位符实现：使用日志记录
            log.info("发送目标达成通知 - 用户: {}, 邮箱: {}, 消息: {}", 
                student.getUsername(), student.getEmail(), message);
            
            // TODO: 实际实现可以调用邮件服务、短信服务或站内消息服务
            
        } catch (Exception e) {
            log.error("发送目标达成通知失败", e);
        }
    }

    @Override
    public void sendReservationCompletionNotice(Reservation reservation) {
        try {
            User student = userMapper.selectById(reservation.getStudentId());
            if (student == null) {
                log.warn("学生不存在，无法发送预约完成通知: {}", reservation.getStudentId());
                return;
            }
            
            String message;
            if (reservation.getReservationType() == ReservationType.COURSE) {
                Course course = courseMapper.selectById(reservation.getCourseId());
                if (course != null) {
                    message = String.format(
                        "【预约完成】尊敬的 %s，您的课程《%s》（%s）已成功完成。感谢您的参与！",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        course.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约完成通知（课程信息未找到）";
                }
            } else {
                Equipment equipment = equipmentMapper.selectById(reservation.getEquipmentId());
                if (equipment != null) {
                    message = String.format(
                        "【预约完成】尊敬的 %s，您的器材使用《%s》（%s - %s）已成功完成。感谢您的使用！",
                        student.getRealName() != null ? student.getRealName() : student.getUsername(),
                        equipment.getName(),
                        reservation.getStartTime().format(DATE_TIME_FORMATTER),
                        reservation.getEndTime().format(DATE_TIME_FORMATTER)
                    );
                } else {
                    message = "预约完成通知（器材信息未找到）";
                }
            }
            
            // 占位符实现：使用日志记录
            log.info("发送预约完成通知 - 用户: {}, 邮箱: {}, 消息: {}", 
                student.getUsername(), student.getEmail(), message);
            
            // TODO: 实际实现可以调用邮件服务、短信服务或站内消息服务
            
        } catch (Exception e) {
            log.error("发送预约完成通知失败", e);
        }
    }
}
