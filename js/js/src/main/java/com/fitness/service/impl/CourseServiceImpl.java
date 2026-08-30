package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.common.ResultCode;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.CourseDTO;
import com.fitness.dto.CourseQueryDTO;
import com.fitness.dto.CreateCourseRequest;
import com.fitness.dto.ScheduleItemDTO;
import com.fitness.dto.UpdateCourseRequest;
import com.fitness.entity.Course;
import com.fitness.entity.Reservation;
import com.fitness.entity.User;
import com.fitness.entity.enums.CourseStatus;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.entity.enums.UserRole;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.CourseMapper;
import com.fitness.mapper.ReservationMapper;
import com.fitness.mapper.StudentProfileMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.CacheService;
import com.fitness.service.CourseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 课程服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CourseServiceImpl implements CourseService {

    private final CourseMapper courseMapper;
    private final UserMapper userMapper;
    private final ReservationMapper reservationMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final CacheService cacheService;
    private final ObjectMapper objectMapper;

    private static final String COURSE_CACHE_PREFIX = "course:";
    private static final String COURSE_LIST_CACHE_PREFIX = "course:list:";
    private static final long COURSE_CACHE_TTL = 10; // 10分钟

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDTO createCourse(CreateCourseRequest request, Long creatorId) {
        log.info("Creating course: {} by user: {}", request.getName(), creatorId);

        // 验证创建者存在
        User creator = userMapper.selectById(creatorId);
        if (creator == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "创建者不存在");
        }

        // 验证权限（管理员或教练）
        if (creator.getRole() != UserRole.ADMIN && creator.getRole() != UserRole.COACH) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员或教练可以创建课程");
        }

        // 验证教练存在
        User coach = userMapper.selectById(request.getCoachId());
        if (coach == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "指定的教练不存在");
        }
        if (coach.getRole() != UserRole.COACH && coach.getRole() != UserRole.ADMIN) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "指定的用户不是教练");
        }

        // 验证时间有效性
        if (request.getEndTime().isBefore(request.getStartTime()) || 
            request.getEndTime().isEqual(request.getStartTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }

        // 转换classSchedule字段
        List<Course.ScheduleItem> scheduleItems = null;
        if (request.getClassSchedule() != null) {
            scheduleItems = request.getClassSchedule().stream()
                    .map(item -> {
                        Course.ScheduleItem scheduleItem = new Course.ScheduleItem();
                        scheduleItem.setDayOfWeek(item.getDayOfWeek());
                        scheduleItem.setStartTime(item.getStartTime());
                        scheduleItem.setEndTime(item.getEndTime());
                        return scheduleItem;
                    })
                    .collect(java.util.stream.Collectors.toList());
        }

        // 创建课程实体
        Course course = Course.builder()
                .name(request.getName())
                .description(request.getDescription())
                .coachId(request.getCoachId())
                .courseType(request.getCourseType())
                .capacity(request.getCapacity())
                .currentEnrollment(0)
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .classSchedule(scheduleItems)
                .location(request.getLocation())
                .status(CourseStatus.AVAILABLE)
                .createdBy(creatorId)
                .imageUrl(request.getImageUrl())
                .build();

        courseMapper.insert(course);
        log.info("Course created successfully with ID: {}", course.getId());

        // 清除课程列表缓存
        clearCourseCache(null);

        return convertToDTO(course, coach.getRealName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CourseDTO updateCourse(Long courseId, UpdateCourseRequest request, Long userId) {
        log.info("Updating course: {} by user: {}", courseId, userId);

        // 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }

        // 验证权限（管理员、课程创建者或课程教练）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (user.getRole() != UserRole.ADMIN && !course.getCreatedBy().equals(userId) && !course.getCoachId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员、课程创建者或课程教练可以更新课程");
        }

        // 更新课程信息
        if (request.getName() != null) {
            course.setName(request.getName());
        }
        // 更新课程信息
        if (request.getImageUrl() != null) {
            course.setImageUrl(request.getImageUrl());
        }
        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }
        if (request.getCoachId() != null) {
            // 验证新教练存在
            User newCoach = userMapper.selectById(request.getCoachId());
            if (newCoach == null) {
                throw new BusinessException(ResultCode.NOT_FOUND, "指定的教练不存在");
            }
            if (newCoach.getRole() != UserRole.COACH && newCoach.getRole() != UserRole.ADMIN) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "指定的用户不是教练");
            }
            course.setCoachId(request.getCoachId());
        }
        if (request.getCourseType() != null) {
            course.setCourseType(request.getCourseType());
        }
        if (request.getCapacity() != null) {
            // 验证新容量不小于当前报名人数
            if (request.getCapacity() < course.getCurrentEnrollment()) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "新容量不能小于当前报名人数");
            }
            course.setCapacity(request.getCapacity());
            // 更新课程状态
            if (course.getCurrentEnrollment() >= request.getCapacity()) {
                course.setStatus(CourseStatus.FULL);
            } else if (course.getStatus() == CourseStatus.FULL) {
                course.setStatus(CourseStatus.AVAILABLE);
            }
        }
        if (request.getStartTime() != null) {
            course.setStartTime(request.getStartTime());
        }
        if (request.getEndTime() != null) {
            course.setEndTime(request.getEndTime());
        }
        if (request.getLocation() != null) {
            course.setLocation(request.getLocation());
        }
        if (request.getStatus() != null) {
            course.setStatus(request.getStatus());
        }
        if (request.getClassSchedule() != null) {
            // 转换classSchedule字段
            List<Course.ScheduleItem> scheduleItems = request.getClassSchedule().stream()
                    .map(item -> {
                        Course.ScheduleItem scheduleItem = new Course.ScheduleItem();
                        scheduleItem.setDayOfWeek(item.getDayOfWeek());
                        scheduleItem.setStartTime(item.getStartTime());
                        scheduleItem.setEndTime(item.getEndTime());
                        return scheduleItem;
                    })
                    .collect(java.util.stream.Collectors.toList());
            course.setClassSchedule(scheduleItems);
        }

        // 验证时间有效性
        if (course.getEndTime().isBefore(course.getStartTime()) || 
            course.getEndTime().isEqual(course.getStartTime())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "结束时间必须晚于开始时间");
        }

        courseMapper.updateById(course);
        log.info("Course updated successfully: {}", courseId);

        // 清除缓存
        clearCourseCache(courseId);

        // 获取教练姓名
        User coach = userMapper.selectById(course.getCoachId());
        return convertToDTO(course, coach != null ? coach.getRealName() : null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteCourse(Long courseId, Long userId) {
        log.info("Deleting course: {} by user: {}", courseId, userId);

        // 查询课程
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }

        // 验证权限（管理员、课程创建者或课程教练）
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        if (user.getRole() != UserRole.ADMIN && !course.getCreatedBy().equals(userId) && !course.getCoachId().equals(userId)) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有管理员、课程创建者或课程教练可以删除课程");
        }

        // 删除所有与该课程相关的预约记录
        List<Reservation> courseReservations = reservationMapper.findByCourseId(courseId);
        for (Reservation reservation : courseReservations) {
            reservationMapper.deleteById(reservation.getId());
            log.info("Deleted reservation: {} for course: {}", reservation.getId(), courseId);
        }

        courseMapper.deleteById(courseId);
        log.info("Course deleted successfully: {}", courseId);

        // 清除缓存
        clearCourseCache(courseId);
    }

    @Override
    public IPage<CourseDTO> getCourseList(Page page, Course query) {

        // 当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        User user = userMapper.selectById(loginUser.getUserId());

        String roleCode = user.getRole().getCode();

        // 如果是教练，只查询自己的课程
        if ("COACH".equals(roleCode)) {
            query.setCoachId(user.getId());
        }

        // 查询课程
        Page<Course> coursePage = courseMapper.selectPage(page, Wrappers.lambdaQuery(query));

        // DTO转换
        IPage<CourseDTO> dtoPage = coursePage.convert(course -> {
            User coach = userMapper.selectById(course.getCoachId());
            return convertToDTO(course, coach != null ? coach.getRealName() : null);
        });

        // 如果是学生，需要判断是否已预约
        if ("STUDENT".equals(roleCode)) {

            List<CourseDTO> records = dtoPage.getRecords();

            if (!records.isEmpty()) {

                // 课程ID集合
                List<Long> courseIds = records.stream()
                        .map(CourseDTO::getId)
                        .toList();

                // 查询该学生的有效预约
                List<Reservation> reservations = reservationMapper.selectList(
                        Wrappers.<Reservation>lambdaQuery()
                                .eq(Reservation::getStudentId, user.getId())
                                .eq(Reservation::getReservationType, ReservationType.COURSE)
                                .in(Reservation::getStatus,
                                        ReservationStatus.PENDING,
                                        ReservationStatus.CONFIRMED)// 关键
                                .in(Reservation::getCourseId, courseIds)
                );

                // 已预约课程ID
                Set<Long> reservedCourseIds = reservations.stream()
                        .map(Reservation::getCourseId)
                        .collect(Collectors.toSet());

                // 修改状态
                for (CourseDTO course : records) {
                    if (reservedCourseIds.contains(course.getId())) {
                        course.setStatus(CourseStatus.RESERVATION);
                    }
                }
            }
        }

        return dtoPage;
    }

    @Override
    public CourseDTO getCourseById(Long courseId) {
        log.debug("Getting course by ID: {}", courseId);

        // 构建缓存键
        String cacheKey = COURSE_CACHE_PREFIX + courseId;

        // 尝试从缓存获取
        try {
            String cachedData = cacheService.get(cacheKey, String.class);
            if (cachedData != null) {
                log.debug("Course found in cache: {}", courseId);
                return objectMapper.readValue(cachedData, CourseDTO.class);
            }
        } catch (Exception e) {
            log.warn("Failed to get course from cache", e);
        }

        // 从数据库查询
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }

        // 获取教练信息
        User coach = userMapper.selectById(course.getCoachId());
        CourseDTO dto = convertToDTO(course, coach != null ? coach.getRealName() : null);

        // 缓存结果
        try {
            String jsonData = objectMapper.writeValueAsString(dto);
            cacheService.set(cacheKey, jsonData, COURSE_CACHE_TTL, TimeUnit.MINUTES);
            log.debug("Course cached successfully: {}", courseId);
        } catch (Exception e) {
            log.warn("Failed to cache course", e);
        }

        return dto;
    }

    @Override
    public void clearCourseCache(Long courseId) {
        if (courseId != null) {
            // 清除特定课程缓存
            String cacheKey = COURSE_CACHE_PREFIX + courseId;
            cacheService.delete(cacheKey);
            log.debug("Cleared cache for course: {}", courseId);
        }
        
        // 清除课程列表缓存
        cacheService.deletePattern(COURSE_LIST_CACHE_PREFIX + "*");
        log.debug("Cleared course list cache");
    }

    /**
     * 转换Course实体为CourseDTO
     */
    private CourseDTO convertToDTO(Course course, String coachName) {
        // 转换classSchedule字段
        List<ScheduleItemDTO> scheduleDTOs = new ArrayList<>();
        if (course.getClassSchedule() != null) {
            scheduleDTOs = course.getClassSchedule().stream()
                    .map(item -> {
                        ScheduleItemDTO dto = new ScheduleItemDTO();
                        dto.setDayOfWeek(item.getDayOfWeek());
                        dto.setStartTime(item.getStartTime());
                        dto.setEndTime(item.getEndTime());
                        return dto;
                    })
                    .collect(java.util.stream.Collectors.toList());
        }

        // 计算总预约数和取消预约数
        List<com.fitness.entity.Reservation> reservations = reservationMapper.selectList(
                com.baomidou.mybatisplus.core.toolkit.Wrappers.<com.fitness.entity.Reservation>lambdaQuery()
                        .eq(com.fitness.entity.Reservation::getCourseId, course.getId())
                        .eq(com.fitness.entity.Reservation::getReservationType, com.fitness.entity.enums.ReservationType.COURSE)
        );
        
        Integer totalReservations = reservations.size();
        Integer cancelledReservations = (int) reservations.stream()
                .filter(r -> com.fitness.entity.enums.ReservationStatus.CANCELLED.equals(r.getStatus()))
                .count();

        return CourseDTO.builder()
                .id(course.getId())
                .name(course.getName())
                .description(course.getDescription())
                .coachId(course.getCoachId())
                .coachName(coachName)
                .courseType(course.getCourseType())
                .capacity(course.getCapacity())
                .currentEnrollment(course.getCurrentEnrollment())
                .availableSlots(course.getCapacity() - course.getCurrentEnrollment())
                .startTime(course.getStartTime())
                .endTime(course.getEndTime())
                .classSchedule(scheduleDTOs)
                .location(course.getLocation())
                .status(course.getStatus())
                .imageUrl(course.getImageUrl())
                .createdBy(course.getCreatedBy())
                .createdAt(course.getCreatedAt())
                .updatedAt(course.getUpdatedAt())
                .totalReservations(totalReservations)
                .cancelledReservations(cancelledReservations)
                .build();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public com.fitness.dto.ReservationDTO enrollCourse(Long studentId, Long courseId) {
        log.info("Student {} enrolling in course {}", studentId, courseId);

        // 验证课程存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }

        // 验证课程状态
        if (course.getStatus() != CourseStatus.AVAILABLE) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程当前不可报名");
        }

        // 验证课程未满
        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "课程已满员");
        }

        // 验证学生存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
        }
        if (student.getRole() != UserRole.STUDENT) {
            throw new BusinessException(ResultCode.FORBIDDEN, "只有学生可以报名课程");
        }

        // 检查是否已报名
        int existingReservation = reservationMapper.countActiveCourseReservationsForStudent(courseId, studentId);
        if (existingReservation > 0) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "您已报名该课程");
        }

        // 检查时间冲突
        List<com.fitness.entity.Reservation> conflictingReservations = reservationMapper.findConflictingCourseReservations(studentId, course.getStartTime(), course.getEndTime());
        if (!conflictingReservations.isEmpty()) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "与已报名的课程时间冲突");
        }

        // 创建预约记录
        com.fitness.entity.Reservation reservation = com.fitness.entity.Reservation.builder()
                .studentId(studentId)
                .reservationType(com.fitness.entity.enums.ReservationType.COURSE)
                .courseId(courseId)
                .startTime(course.getStartTime())
                .endTime(course.getEndTime())
                .status(com.fitness.entity.enums.ReservationStatus.CONFIRMED)
                .build();
        reservationMapper.insert(reservation);

        // 更新课程报名人数
        course.setCurrentEnrollment(course.getCurrentEnrollment() + 1);
        if (course.getCurrentEnrollment() >= course.getCapacity()) {
            course.setStatus(CourseStatus.FULL);
        }
        courseMapper.updateById(course);

        log.info("Student {} enrolled in course {}", studentId, courseId);

        // 返回预约DTO
        return convertReservationToDTO(reservation, course);
    }

    @Override
    public java.util.List<com.fitness.dto.CourseDTO> getEnrolledCourses(Long studentId) {
        log.info("Getting enrolled courses for student {}", studentId);

        // 验证学生存在
        User student = userMapper.selectById(studentId);
        if (student == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
        }

        // 获取学生的预约记录
        List<com.fitness.entity.Reservation> reservations = reservationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.fitness.entity.Reservation>()
                        .eq(com.fitness.entity.Reservation::getStudentId, studentId)
                        .eq(com.fitness.entity.Reservation::getReservationType, com.fitness.entity.enums.ReservationType.COURSE)
                        .eq(com.fitness.entity.Reservation::getStatus, com.fitness.entity.enums.ReservationStatus.CONFIRMED)
        );

        if (reservations.isEmpty()) {
            return List.of();
        }

        // 获取课程ID列表
        List<Long> courseIds = reservations.stream()
                .map(com.fitness.entity.Reservation::getCourseId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询课程
        List<Course> courses = courseMapper.selectBatchIds(courseIds);

        // 转换为DTO
        return courses.stream()
                .map(course -> {
                    User coach = userMapper.selectById(course.getCoachId());
                    return convertToDTO(course, coach != null ? coach.getRealName() : null);
                })
                .collect(Collectors.toList());
    }

    @Override
    public java.util.List<com.fitness.dto.UserDTO> getEnrolledStudents(Long courseId) {
        log.info("Getting enrolled students for course {}", courseId);

        // 验证课程存在
        Course course = courseMapper.selectById(courseId);
        if (course == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "课程不存在");
        }

        // 获取课程的预约记录
        List<com.fitness.entity.Reservation> reservations = reservationMapper.selectList(
                new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<com.fitness.entity.Reservation>()
                        .eq(com.fitness.entity.Reservation::getCourseId, courseId)
                        .eq(com.fitness.entity.Reservation::getReservationType, com.fitness.entity.enums.ReservationType.COURSE)
                        .eq(com.fitness.entity.Reservation::getStatus, com.fitness.entity.enums.ReservationStatus.CONFIRMED)
        );

        if (reservations.isEmpty()) {
            return List.of();
        }

        // 获取学生ID列表
        List<Long> studentIds = reservations.stream()
                .map(com.fitness.entity.Reservation::getStudentId)
                .distinct()
                .collect(Collectors.toList());

        // 批量查询学生
        List<User> students = userMapper.selectBatchIds(studentIds);

        // 转换为DTO
        return students.stream()
                .map(this::convertUserToDTO)
                .collect(Collectors.toList());
    }

    /**
     * 转换Reservation实体为ReservationDTO
     */
    private com.fitness.dto.ReservationDTO convertReservationToDTO(com.fitness.entity.Reservation reservation, Course course) {
        com.fitness.dto.ReservationDTO dto = new com.fitness.dto.ReservationDTO();
        dto.setId(reservation.getId());
        dto.setStudentId(reservation.getStudentId());
        dto.setReservationType(reservation.getReservationType());
        dto.setCourseId(reservation.getCourseId());
        dto.setStartTime(reservation.getStartTime());
        dto.setEndTime(reservation.getEndTime());
        dto.setStatus(reservation.getStatus());
        
        // 设置课程信息
        if (course != null) {
            dto.setCourseName(course.getName());
            dto.setLocation(course.getLocation());
        }
        
        return dto;
    }

    /**
     * 转换User实体为UserDTO
     */
    private com.fitness.dto.UserDTO convertUserToDTO(User user) {
        com.fitness.dto.UserDTO dto = new com.fitness.dto.UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setPhone(user.getPhone());
        dto.setRealName(user.getRealName());
        dto.setRole(user.getRole() != null ? user.getRole().getCode() : null);
        dto.setStatus(user.getStatus());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        
        // 如果是学生，加载学生信息
        if (user.getRole() == com.fitness.entity.enums.UserRole.STUDENT) {
            com.fitness.entity.StudentProfile studentProfile = 
                    studentProfileMapper.findByUserId(user.getId()).orElse(null);
            if (studentProfile != null) {
                com.fitness.dto.StudentProfileDTO studentDTO = new com.fitness.dto.StudentProfileDTO();
                BeanUtils.copyProperties(studentProfile, studentDTO);
                dto.setStudentProfile(studentDTO);
            }
        }
        
        return dto;
    }
}