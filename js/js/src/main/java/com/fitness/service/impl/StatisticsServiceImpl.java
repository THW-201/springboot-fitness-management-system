package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.dto.*;
import com.fitness.entity.*;
import com.fitness.entity.enums.ReservationStatus;
import com.fitness.entity.enums.ReservationType;
import com.fitness.mapper.*;
import com.fitness.entity.PlanCheckIn;
import com.fitness.service.CacheService;
import com.fitness.service.StatisticsService;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final CourseMapper courseMapper;
    private final EquipmentMapper equipmentMapper;
    private final ReservationMapper reservationMapper;
    private final CheckInMapper checkInMapper;
    private final UserMapper userMapper;
    private final HealthPlanMapper healthPlanMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final PlanCheckInMapper planCheckInMapper;
    private final CacheService cacheService;

    /**
     * 根据角色处理查询范围
     */
    private StatisticsQueryDTO handleQueryByRole(StatisticsQueryDTO query) {
        LoginUser loginUser = SecurityUtils.getLoginUser();

        User user = userMapper.selectById(loginUser.getUserId());
        if (user == null) return query;

        String roleCode = user.getRole().getCode();

        if ("COACH".equals(roleCode) && query.getCoachId() == null) {
            query.setCoachId(user.getId());
        } else if ("STUDENT".equals(roleCode) && query.getStudentId() == null) {
            query.setStudentId(user.getId());
        }
        return query;
    }

    @Override
    public List<CourseStatisticsDTO> getCourseStatistics(StatisticsQueryDTO query) {
        query = handleQueryByRole(query);

        LambdaQueryWrapper<Course> wrapper = Wrappers.lambdaQuery(Course.class);

        if (query.getStartDate() != null) {
            wrapper.ge(Course::getCreatedAt, query.getStartDate().atStartOfDay());
        }
        if (query.getEndDate() != null) {
            wrapper.le(Course::getCreatedAt, query.getEndDate().atTime(LocalTime.MAX));
        }
        if (query.getCoachId() != null) {
            wrapper.eq(Course::getCoachId, query.getCoachId());
        }

        List<Course> courses = courseMapper.selectList(wrapper);
        List<CourseStatisticsDTO> result = new ArrayList<>();

        for (Course course : courses) {
            List<Reservation> reservations = reservationMapper.selectList(
                    Wrappers.<Reservation>lambdaQuery()
                            .eq(Reservation::getCourseId, course.getId())
                            .eq(Reservation::getReservationType, ReservationType.COURSE)
            );

            Long totalReservations = (long) reservations.size();
            Long cancelledReservations = reservations.stream()
                    .filter(r -> ReservationStatus.CANCELLED.equals(r.getStatus()))
                    .count();
            Long actualReservations = totalReservations - cancelledReservations;

            List<Long> reservationIds = reservations.stream()
                    .map(Reservation::getId)
                    .collect(Collectors.toList());

            Long totalCheckIns = 0L;
            if (!reservationIds.isEmpty()) {
                totalCheckIns = checkInMapper.selectCount(
                        Wrappers.<CheckIn>lambdaQuery()
                                .in(!reservationIds.isEmpty(), CheckIn::getReservationId, reservationIds)
                );
            }

            BigDecimal checkInRate = BigDecimal.ZERO;
            if (totalReservations > 0) {
                checkInRate = BigDecimal.valueOf(totalCheckIns)
                        .divide(BigDecimal.valueOf(totalReservations), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            BigDecimal utilizationRate = BigDecimal.ZERO;
            if (course.getCapacity() > 0) {
                utilizationRate = BigDecimal.valueOf(totalReservations)
                        .divide(BigDecimal.valueOf(course.getCapacity()), 4, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            result.add(CourseStatisticsDTO.builder()
                    .courseId(course.getId())
                    .courseName(course.getName())
                    .totalReservations(totalReservations.intValue())
                    .cancelledReservations(cancelledReservations.intValue())
                    .actualReservations(actualReservations.intValue())
                    .totalCheckIns(totalCheckIns.intValue())
                    .checkInRate(checkInRate)
                    .capacity(course.getCapacity())
                    .utilizationRate(utilizationRate)
                    .createdAt(course.getCreatedAt())
                    .updatedAt(course.getUpdatedAt())
                    .build());
        }
        return result;
    }

    @Override
    public List<EquipmentStatisticsDTO> getEquipmentStatistics(StatisticsQueryDTO query) {
        query = handleQueryByRole(query);

        LambdaQueryWrapper<Reservation> resWrapper = Wrappers.lambdaQuery(Reservation.class);
        if (query.getCoachId()!=null){
            // 1. 查询教练负责的学生
            List<StudentProfile> studentProfiles = studentProfileMapper.selectList(Wrappers.lambdaQuery(StudentProfile.class)
                    .eq(StudentProfile::getCoachId, query.getCoachId()));
            
            // 2. 查询教练负责的器材
            List<Equipment> coachEquipments = equipmentMapper.selectList(Wrappers.lambdaQuery(Equipment.class)
                    .eq(Equipment::getCoachId, query.getCoachId()));
            
            // 3. 提取学生ID和器材ID
            Set<Long> studentIdSet = new HashSet<>();
            Set<Long> equipmentIdSet = new HashSet<>();
            
            // 添加教练负责的学生
            if (!studentProfiles.isEmpty()){
                studentProfiles.forEach(profile -> studentIdSet.add(profile.getUserId()));
            }
            
            // 添加教练负责的器材
            if (!coachEquipments.isEmpty()){
                coachEquipments.forEach(equipment -> equipmentIdSet.add(equipment.getId()));
            }
            
            // 4. 应用过滤条件
            if (!studentIdSet.isEmpty()){
                resWrapper.in(Reservation::getStudentId, studentIdSet);
            }
            if (!equipmentIdSet.isEmpty()){
                resWrapper.in(Reservation::getEquipmentId, equipmentIdSet);
            }
        }
        if (query.getStartDate() != null) {
            resWrapper.ge(Reservation::getStartTime, query.getStartDate().atStartOfDay());
        }
        if (query.getEndDate() != null) {
            resWrapper.le(Reservation::getEndTime, query.getEndDate().atTime(LocalTime.MAX));
        }
        if (query.getStudentId() != null) {
            resWrapper.eq(Reservation::getStudentId, query.getStudentId());
        }
        resWrapper.eq(Reservation::getReservationType, ReservationType.EQUIPMENT);

        List<Reservation> allReservations = reservationMapper.selectList(resWrapper);

        // 只查询教练负责的器材
        LambdaQueryWrapper<Equipment> equipmentWrapper = Wrappers.lambdaQuery(Equipment.class);
        if (query.getCoachId() != null) {
            equipmentWrapper.eq(Equipment::getCoachId, query.getCoachId());
        }
        List<Equipment> equipmentList = equipmentMapper.selectList(equipmentWrapper);
        List<EquipmentStatisticsDTO> result = new ArrayList<>();

        for (Equipment equipment : equipmentList) {
            List<Reservation> equipmentReservations = allReservations.stream()
                    .filter(r -> Objects.equals(r.getEquipmentId(), equipment.getId()))
                    .toList();

            int totalReservations = equipmentReservations.size();
            int totalUsageMinutes = 0;

            for (Reservation r : equipmentReservations) {
                List<CheckIn> checkIns = checkInMapper.selectList(
                        Wrappers.<CheckIn>lambdaQuery().eq(CheckIn::getReservationId, r.getId())
                );
                for (CheckIn c : checkIns) {
                    if (c.getDurationMinutes() != null) totalUsageMinutes += c.getDurationMinutes();
                }
            }

            BigDecimal averageUsage = totalReservations > 0 ?
                    BigDecimal.valueOf(totalUsageMinutes).divide(BigDecimal.valueOf(totalReservations), 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            result.add(EquipmentStatisticsDTO.builder()
                    .equipmentId(equipment.getId())
                    .equipmentName(equipment.getName())
                    .totalReservations(totalReservations)
                    .totalUsageMinutes(totalUsageMinutes)
                    .averageUsageMinutes(averageUsage)
                    .usageFrequency(totalReservations)
                    .createdAt(equipment.getCreatedAt())
                    .updatedAt(equipment.getUpdatedAt())
                    .build());
        }

        return result;
    }

    @Override
    public Page<StudentStatisticsDTO> getStudentStatistics(Page page, StatisticsQueryDTO query) {

        query = handleQueryByRole(query);
        LambdaQueryWrapper<User> userWrapper = Wrappers.lambdaQuery(User.class)
                .eq(User::getRole, "STUDENT");
        if (query.getCoachId()!=null){
            // 1. 查询分配给教练的学生
            List<StudentProfile> studentProfiles = studentProfileMapper.selectList(Wrappers.lambdaQuery(StudentProfile.class)
                    .eq(StudentProfile::getCoachId, query.getCoachId()));
            
            // 2. 查询教练的课程
            List<Course> courses = courseMapper.selectList(Wrappers.lambdaQuery(Course.class)
                    .eq(Course::getCoachId, query.getCoachId()));
            
            // 3. 提取学生ID列表
            Set<Long> studentIdSet = new HashSet<>();
            
            // 添加分配的学生
            if (!studentProfiles.isEmpty()){
                studentProfiles.forEach(profile -> studentIdSet.add(profile.getUserId()));
            }
            
            // 添加参加课程的学生
            if (!courses.isEmpty()) {
                List<Long> courseIds = courses.stream().map(Course::getId).collect(Collectors.toList());
                List<Reservation> reservations = reservationMapper.selectList(
                        Wrappers.lambdaQuery(Reservation.class)
                                .in(Reservation::getCourseId, courseIds)
                                .eq(Reservation::getReservationType, ReservationType.COURSE)
                );
                reservations.forEach(reservation -> studentIdSet.add(reservation.getStudentId()));
            }
            
            // 4. 应用学生ID过滤
            if (!studentIdSet.isEmpty()){
                userWrapper.in(User::getId, studentIdSet);
            }
        }
        if (query.getStudentId() != null) {
            userWrapper.eq(User::getId, query.getStudentId());
        }

        Page<User> students = userMapper.selectPage(page, userWrapper);

        if (students.getRecords().isEmpty()) {
            return new Page<>();
        }

        List<Long> studentIds = students.getRecords()
                .stream()
                .map(User::getId)
                .toList();

        /*
         * 查询所有打卡记录
         */
        LambdaQueryWrapper<CheckIn> checkInWrapper = Wrappers.lambdaQuery(CheckIn.class)
                .in(CheckIn::getStudentId, studentIds);

        if (query.getStartDate() != null) {
            checkInWrapper.ge(CheckIn::getCheckInTime, query.getStartDate().atStartOfDay());
        }

        if (query.getEndDate() != null) {
            checkInWrapper.le(CheckIn::getCheckInTime, query.getEndDate().atTime(LocalTime.MAX));
        }

        List<CheckIn> checkIns = checkInMapper.selectList(checkInWrapper);

        /*
         * 查询所有预约记录
         */
        LambdaQueryWrapper<Reservation> reservationWrapper = Wrappers.lambdaQuery(Reservation.class)
                .in(Reservation::getStudentId, studentIds);

        if (query.getStartDate() != null) {
            reservationWrapper.ge(Reservation::getStartTime, query.getStartDate().atStartOfDay());
        }

        if (query.getEndDate() != null) {
            reservationWrapper.le(Reservation::getEndTime, query.getEndDate().atTime(LocalTime.MAX));
        }

        List<Reservation> reservations = reservationMapper.selectList(reservationWrapper);

        /*
         * 按 studentId 分组
         */
        Map<Long, List<CheckIn>> checkInMap =
                checkIns.stream().collect(Collectors.groupingBy(CheckIn::getStudentId));

        Map<Long, Long> reservationCountMap =
                reservations.stream().collect(Collectors.groupingBy(
                        Reservation::getStudentId,
                        Collectors.counting()
                ));

        List<StudentStatisticsDTO> result = new ArrayList<>();

        for (User student : students.getRecords()) {

            Long studentId = student.getId();

            List<CheckIn> studentCheckIns =
                    checkInMap.getOrDefault(studentId, Collections.emptyList());

            int totalExerciseMinutes = studentCheckIns.stream()
                    .filter(c -> c.getDurationMinutes() != null)
                    .mapToInt(CheckIn::getDurationMinutes)
                    .sum();

            BigDecimal totalCalories = studentCheckIns.stream()
                    .filter(c -> c.getCaloriesBurned() != null)
                    .map(CheckIn::getCaloriesBurned)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            int totalReservations =
                    reservationCountMap.getOrDefault(studentId, 0L).intValue();

            int activityScore =
                    (studentCheckIns.size() * 10) + (totalExerciseMinutes / 60);

            StudentStatisticsDTO dto = StudentStatisticsDTO.builder()
                    .studentId(studentId)
                    .studentName(
                            student.getRealName() != null
                                    ? student.getRealName()
                                    : student.getUsername()
                    )
                    .totalReservations(totalReservations)
                    .totalCheckIns(studentCheckIns.size())
                    .totalExerciseMinutes(totalExerciseMinutes)
                    .totalCaloriesBurned(totalCalories)
                    .activityScore(activityScore)
                    .build();

            result.add(dto);
        }

        Page<StudentStatisticsDTO> resultPage =
                new Page<>(students.getCurrent(), students.getSize(), students.getTotal());

        resultPage.setRecords(result);

        return resultPage;
    }

    @Override
    public PersonalHealthDataDTO getPersonalHealthData(Long studentId) {
        // 查询常规签到记录
        List<CheckIn> allCheckIns = checkInMapper.selectList(
                Wrappers.<CheckIn>lambdaQuery()
                        .eq(CheckIn::getStudentId, studentId)
                        .orderByDesc(CheckIn::getCheckInTime)
        );

        // 查询健康计划打卡记录
        List<PlanCheckIn> allPlanCheckIns = planCheckInMapper.selectList(
                Wrappers.<PlanCheckIn>lambdaQuery()
                        .eq(PlanCheckIn::getStudentId, studentId)
                        .eq(PlanCheckIn::getStatus, "COMPLETED")
                        .orderByDesc(PlanCheckIn::getCheckInTime)
        );

        // 计算总运动分钟数（常规签到 + 健康计划打卡）
        int totalExerciseMinutes = allCheckIns.stream().filter(c -> c.getDurationMinutes() != null)
                .mapToInt(CheckIn::getDurationMinutes).sum();
        totalExerciseMinutes += allPlanCheckIns.stream().filter(pc -> pc.getDurationMinutes() != null)
                .mapToInt(PlanCheckIn::getDurationMinutes).sum();

        // 计算总消耗卡路里（常规签到 + 健康计划打卡）
        BigDecimal totalCalories = allCheckIns.stream().filter(c -> c.getCaloriesBurned() != null)
                .map(CheckIn::getCaloriesBurned).reduce(BigDecimal.ZERO, BigDecimal::add);
        totalCalories = totalCalories.add(
                allPlanCheckIns.stream().filter(pc -> pc.getCaloriesBurned() != null)
                        .map(pc -> java.math.BigDecimal.valueOf(pc.getCaloriesBurned()))
                        .reduce(java.math.BigDecimal.ZERO, java.math.BigDecimal::add)
        );

        List<HealthPlan> healthPlans = healthPlanMapper.selectList(
                Wrappers.<HealthPlan>lambdaQuery()
                        .eq(HealthPlan::getStudentId, studentId)
                        .orderByDesc(HealthPlan::getUpdatedAt)
        );

        List<PersonalHealthDataDTO.WeightRecord> weightHistory = new ArrayList<>();
        for (HealthPlan plan : healthPlans) {
            if (plan.getCurrentWeight() != null) {
                weightHistory.add(PersonalHealthDataDTO.WeightRecord.builder()
                        .date(plan.getUpdatedAt().toLocalDate().toString())
                        .weight(plan.getCurrentWeight())
                        .build());
            }
        }

        // 课程参与历史
        List<PersonalHealthDataDTO.CourseParticipation> courseHistory = new ArrayList<>();
        List<Reservation> courseReservations = reservationMapper.selectList(
                Wrappers.<Reservation>lambdaQuery()
                        .eq(Reservation::getStudentId, studentId)
                        .eq(Reservation::getReservationType, ReservationType.COURSE)
                        .orderByDesc(Reservation::getStartTime)
                        .last("LIMIT 20")
        );

        Map<Long, CheckIn> checkInMap = checkInMapper.selectList(
                Wrappers.<CheckIn>lambdaQuery().in(!courseReservations.isEmpty(), CheckIn::getReservationId,
                        courseReservations.stream().map(Reservation::getId).collect(Collectors.toList()))
        ).stream().collect(Collectors.toMap(CheckIn::getReservationId, c -> c, (a, b) -> a));

        for (Reservation r : courseReservations) {
            Course course = courseMapper.selectById(r.getCourseId());
            if (course != null) {
                CheckIn c = checkInMap.get(r.getId());
                courseHistory.add(PersonalHealthDataDTO.CourseParticipation.builder()
                        .courseId(course.getId())
                        .courseName(course.getName())
                        .date(r.getStartTime().toLocalDate().toString())
                        .durationMinutes(c != null ? c.getDurationMinutes() : null)
                        .build());
            }
        }

        // 器材使用历史
        List<PersonalHealthDataDTO.EquipmentUsage> equipmentHistory = new ArrayList<>();
        List<Reservation> equipmentReservations = reservationMapper.selectList(
                Wrappers.<Reservation>lambdaQuery()
                        .eq(Reservation::getStudentId, studentId)
                        .eq(Reservation::getReservationType, ReservationType.EQUIPMENT)
                        .orderByDesc(Reservation::getStartTime)
                        .last("LIMIT 20")
        );

        Map<Long, CheckIn> equipmentCheckInMap = checkInMapper.selectList(
                Wrappers.<CheckIn>lambdaQuery().in(!equipmentReservations.isEmpty(), CheckIn::getReservationId,
                        equipmentReservations.stream().map(Reservation::getId).collect(Collectors.toList()))
        ).stream().collect(Collectors.toMap(CheckIn::getReservationId, c -> c, (a, b) -> a));

        for (Reservation r : equipmentReservations) {
            Equipment e = equipmentMapper.selectById(r.getEquipmentId());
            if (e != null) {
                CheckIn c = equipmentCheckInMap.get(r.getId());
                equipmentHistory.add(PersonalHealthDataDTO.EquipmentUsage.builder()
                        .equipmentId(e.getId())
                        .equipmentName(e.getName())
                        .date(r.getStartTime().toLocalDate().toString())
                        .durationMinutes(c != null ? c.getDurationMinutes() : null)
                        .build());
            }
        }

        // 计算总打卡次数（常规签到 + 健康计划打卡）
        int totalExerciseCount = allCheckIns.size() + allPlanCheckIns.size();

        return PersonalHealthDataDTO.builder()
                .studentId(studentId)
                .totalExerciseMinutes(totalExerciseMinutes)
                .totalExerciseCount(totalExerciseCount)
                .totalCaloriesBurned(totalCalories)
                .weightHistory(weightHistory)
                .courseHistory(courseHistory)
                .equipmentHistory(equipmentHistory)
                .build();
    }
}