package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.fitness.entity.HealthAdvice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 健康建议 Mapper 接口
 * 提供健康建议数据访问操作
 */
@Mapper
public interface HealthAdviceMapper extends BaseMapper<HealthAdvice> {

    /**
     * 根据学生ID查询健康建议列表
     *
     * @param studentId 学生ID
     * @return 健康建议列表
     */
    @Select("SELECT * FROM health_advice WHERE student_id = #{studentId} ORDER BY created_at DESC")
    List<HealthAdvice> findByStudentId(Long studentId);

    /**
     * 根据学生ID查询最近的健康建议
     *
     * @param studentId 学生ID
     * @param limit 限制数量
     * @return 健康建议列表
     */
    @Select("SELECT * FROM health_advice WHERE student_id = #{studentId} ORDER BY created_at DESC LIMIT #{limit}")
    List<HealthAdvice> findRecentByStudentId(@Param("studentId") Long studentId, @Param("limit") int limit);

    /**
     * 根据学生ID和建议类型查询健康建议
     *
     * @param studentId 学生ID
     * @param adviceType 建议类型
     * @return 健康建议列表
     */
    @Select("SELECT * FROM health_advice WHERE student_id = #{studentId} AND advice_type = #{adviceType} ORDER BY created_at DESC")
    List<HealthAdvice> findByStudentIdAndType(@Param("studentId") Long studentId, @Param("adviceType") String adviceType);

    /**
     * 根据学生ID和时间范围查询健康建议
     *
     * @param studentId 学生ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 健康建议列表
     */
    @Select("SELECT * FROM health_advice WHERE student_id = #{studentId} " +
            "AND created_at >= #{startTime} AND created_at <= #{endTime} " +
            "ORDER BY created_at DESC")
    List<HealthAdvice> findByStudentIdAndTimeRange(@Param("studentId") Long studentId,
                                                    @Param("startTime") LocalDateTime startTime,
                                                    @Param("endTime") LocalDateTime endTime);
}
