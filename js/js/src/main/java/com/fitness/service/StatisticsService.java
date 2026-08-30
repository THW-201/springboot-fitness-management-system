package com.fitness.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.dto.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 统计服务接口
 */
public interface StatisticsService {

    List<CourseStatisticsDTO> getCourseStatistics(StatisticsQueryDTO query);

    List<EquipmentStatisticsDTO> getEquipmentStatistics(StatisticsQueryDTO query);

    Page<StudentStatisticsDTO> getStudentStatistics(Page page,StatisticsQueryDTO query);

    PersonalHealthDataDTO getPersonalHealthData(Long studentId);

}