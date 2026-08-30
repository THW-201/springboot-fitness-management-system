package com.fitness.security;

import com.fitness.common.ResultCode;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.entity.enums.UserRole;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.StudentProfileMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 基于角色的访问控制组件
 * 提供权限验证和数据隔离功能
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RoleBasedAccessControl {

    private final StudentProfileMapper studentProfileMapper;

    /**
     * 检查用户是否有权限访问指定资源
     *
     * @param resource 资源类型
     * @param action   操作类型
     * @return 是否有权限
     */
    public boolean hasPermission(String resource, String action) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return false;
        }

        // 管理员拥有所有权限
        if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_ADMIN")) {
            return true;
        }

        // 教练权限
        if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_COACH")) {
            return hasCoachPermission(resource, action);
        }

        // 学生权限
        if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_STUDENT")) {
            return hasStudentPermission(resource, action);
        }

        return false;
    }

    /**
     * 检查教练是否有权限访问指定学生数据
     *
     * @param coachId   教练ID
     * @param studentId 学生ID
     * @return 是否有权限
     */
    public boolean canAccessStudentData(Long coachId, Long studentId) {
        if (coachId == null || studentId == null) {
            return false;
        }

        // 查询学生的负责教练ID
        Long studentCoachId = studentProfileMapper.getCoachIdByStudentId(studentId);
        
        // 检查是否是该教练负责的学生
        boolean hasAccess = coachId.equals(studentCoachId);
        
        if (!hasAccess) {
            log.warn("Coach {} attempted to access student {} data without permission", coachId, studentId);
        }
        
        return hasAccess;
    }

    /**
     * 验证教练是否有权限访问学生数据，无权限则抛出异常
     *
     * @param coachId   教练ID
     * @param studentId 学生ID
     * @throws BusinessException 无权限时抛出
     */
    public void validateCoachAccess(Long coachId, Long studentId) {
        if (!canAccessStudentData(coachId, studentId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "无权访问该学生数据");
        }
    }

    /**
     * 检查教练是否有指定资源的权限
     */
    private boolean hasCoachPermission(String resource, String action) {
        // 教练可以管理课程
        if ("course".equals(resource)) {
            return "create".equals(action) || "update".equals(action) || "delete".equals(action) || "read".equals(action);
        }

        // 教练可以查看自己负责的学生数据
        if ("student".equals(resource)) {
            return "read".equals(action);
        }

        // 教练可以查看预约信息
        if ("reservation".equals(resource)) {
            return "read".equals(action);
        }

        // 教练可以查看签到信息
        if ("checkin".equals(resource)) {
            return "read".equals(action);
        }

        return false;
    }

    /**
     * 检查学生是否有指定资源的权限
     */
    private boolean hasStudentPermission(String resource, String action) {
        // 学生只能读取课程和器材信息
        if ("course".equals(resource) || "equipment".equals(resource)) {
            return "read".equals(action);
        }

        // 学生可以管理自己的预约
        if ("reservation".equals(resource)) {
            return "create".equals(action) || "read".equals(action) || "delete".equals(action);
        }

        // 学生可以管理自己的签到
        if ("checkin".equals(resource)) {
            return "create".equals(action) || "read".equals(action) || "update".equals(action);
        }

        // 学生可以管理自己的健康计划
        if ("healthplan".equals(resource)) {
            return "create".equals(action) || "read".equals(action) || "update".equals(action) || "delete".equals(action);
        }

        // 学生可以使用AI服务
        if ("ai".equals(resource)) {
            return "read".equals(action) || "create".equals(action);
        }

        return false;
    }

    /**
     * 从认证信息中获取用户ID
     *
     * @return 用户ID
     */
    public Long getUserId() {
        return SecurityUtils.getUserId();
    }

    /**
     * 从认证信息中获取用户角色
     *
     * @return 用户角色
     */
    public UserRole getUserRole() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }

        if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_ADMIN")) {
            return UserRole.ADMIN;
        } else if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_COACH")) {
            return UserRole.COACH;
        } else if (loginUser.getRoles() != null && loginUser.getRoles().contains("ROLE_STUDENT")) {
            return UserRole.STUDENT;
        }

        return null;
    }
}