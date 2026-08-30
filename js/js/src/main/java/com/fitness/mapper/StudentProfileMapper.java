package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.StudentProfile;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Optional;

/**
 * 学生信息 Mapper 接口
 * 提供学生信息数据访问操作
 */
@Mapper
public interface StudentProfileMapper extends BaseMapper<StudentProfile> {

    /**
     * 根据用户ID查询学生信息
     *
     * @param userId 用户ID
     * @return 学生信息
     */
    @Select("SELECT * FROM student_profiles WHERE user_id = #{userId}")
    Optional<StudentProfile> findByUserId(Long userId);

    /**
     * 根据学号查询学生信息
     *
     * @param studentNumber 学号
     * @return 学生信息
     */
    @Select("SELECT * FROM student_profiles WHERE student_number = #{studentNumber}")
    Optional<StudentProfile> findByStudentNumber(String studentNumber);

    /**
     * 根据教练ID查询学生列表
     * 用于教练查看自己负责的学生
     *
     * @param coachId 教练ID
     * @return 学生信息列表
     */
    @Select("SELECT * FROM student_profiles WHERE coach_id = #{coachId} ORDER BY created_at DESC")
    List<StudentProfile> findByCoachId(Long coachId);

    /**
     * 检查学号是否存在
     *
     * @param studentNumber 学号
     * @return 是否存在
     */
    @Select("SELECT COUNT(*) > 0 FROM student_profiles WHERE student_number = #{studentNumber}")
    boolean existsByStudentNumber(String studentNumber);

    /**
     * 根据学生ID获取负责教练ID
     * 用于权限验证
     *
     * @param studentId 学生用户ID
     * @return 教练ID
     */
    @Select("SELECT coach_id FROM student_profiles WHERE user_id = #{studentId}")
    Long getCoachIdByStudentId(Long studentId);

    /**
     * 增加学生的迟到取消次数
     *
     * @param studentId 学生用户ID
     * @return 影响行数
     */
    @Select("UPDATE student_profiles SET late_cancellation_count = late_cancellation_count + 1 WHERE user_id = #{studentId}")
    int incrementLateCancellationCount(Long studentId);
}
