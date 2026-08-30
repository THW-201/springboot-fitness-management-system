package com.fitness.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.dto.CourseDTO;
import com.fitness.dto.CourseQueryDTO;
import com.fitness.dto.CreateCourseRequest;
import com.fitness.dto.UpdateCourseRequest;
import com.fitness.entity.Course;
import com.fitness.entity.enums.CourseStatus;

/**
 * 课程服务接口
 * 提供课程管理的业务逻辑
 */
public interface CourseService {

    /**
     * 创建课程
     * 记录创建者信息，验证权限（管理员或教练）
     *
     * @param request 创建课程请求
     * @param creatorId 创建者ID
     * @return 课程信息
     */
    CourseDTO createCourse(CreateCourseRequest request, Long creatorId);

    /**
     * 更新课程
     * 验证权限（管理员或课程创建者）
     *
     * @param courseId 课程ID
     * @param request 更新课程请求
     * @param userId 当前用户ID
     * @return 更新后的课程信息
     */
    CourseDTO updateCourse(Long courseId, UpdateCourseRequest request, Long userId);

    /**
     * 删除课程
     * 检查是否存在未完成的预约，验证权限（管理员或课程创建者）
     *
     * @param courseId 课程ID
     * @param userId 当前用户ID
     */
    void deleteCourse(Long courseId, Long userId);

    /**
     * 获取课程列表
     * 支持分页、搜索、筛选
     *
     * @param page 分页对象
     * @param query 查询条件对象
     * @return 课程分页列表
     */
    IPage<CourseDTO> getCourseList(Page page, Course query);

    /**
     * 获取课程详情
     *
     * @param courseId 课程ID
     * @return 课程详情
     */
    CourseDTO getCourseById(Long courseId);

    /**
     * 清除课程相关缓存
     *
     * @param courseId 课程ID（可选，为null时清除所有课程缓存）
     */
    void clearCourseCache(Long courseId);

    /**
     * 学生报名课程
     *
     * @param studentId 学生ID
     * @param courseId 课程ID
     * @return 预约信息
     */
    com.fitness.dto.ReservationDTO enrollCourse(Long studentId, Long courseId);

    /**
     * 查询学生已报名的课程列表
     *
     * @param studentId 学生ID
     * @return 课程列表
     */
    java.util.List<com.fitness.dto.CourseDTO> getEnrolledCourses(Long studentId);

    /**
     * 查询课程已报名的学生列表
     *
     * @param courseId 课程ID
     * @return 学生列表
     */
    java.util.List<com.fitness.dto.UserDTO> getEnrolledStudents(Long courseId);
}
