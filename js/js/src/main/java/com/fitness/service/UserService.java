package com.fitness.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.dto.UserDTO;
import com.fitness.dto.UpdateUserRequest;
import com.fitness.dto.UserQueryDTO;

import java.util.List;

/**
 * 用户管理服务接口
 */
public interface UserService {

    /**
     * 获取用户列表（分页）
     * 仅管理员可访问
     *
     * @param page  分页对象
     * @param query 查询条件对象
     * @return 用户列表
     */
    Page<UserDTO> getUserList(Page<UserDTO> page, com.fitness.dto.UserQueryDTO query);

    /**
     * 获取用户详情
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    UserDTO getUserById(Long userId);

    /**
     * 更新用户信息
     *
     * @param userId  用户ID
     * @param request 更新请求
     * @return 更新后的用户信息
     */
    UserDTO updateUser(Long userId, UpdateUserRequest request);

    /**
     * 删除用户
     * 仅管理员可访问
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);


    /**
     * 分页查询教练负责的学生列表
     * 仅教练可访问
     *
     * @param page    分页对象
     * @param query   查询条件
     * @param coachId 教练ID
     * @return 学生分页列表
     */
    Page<UserDTO> getStudentsByCoachIdWithPage(Page page, UserQueryDTO query, Long coachId);

    /**
     * 为学生分配教练
     * 仅管理员可访问
     *
     * @param studentId 学生ID
     * @param coachId   教练ID
     */
    void assignCoachToStudent(Long studentId, Long coachId);
    
    /**
     * 获取学生档案
     *
     * @param userId 用户ID
     * @return 学生档案
     */
    com.fitness.dto.StudentProfileDTO getStudentProfile(Long userId);
    
    /**
     * 更新学生档案
     *
     * @param userId  用户ID
     * @param request 学生档案更新请求
     * @return 更新后的学生档案
     */
    com.fitness.dto.StudentProfileDTO updateStudentProfile(Long userId, com.fitness.dto.StudentProfileDTO request);
}
