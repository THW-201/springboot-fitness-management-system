package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.entity.AuditLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 审计日志 Mapper 接口
 * 提供审计日志数据访问操作
 */
@Mapper
public interface AuditLogMapper extends BaseMapper<AuditLog> {

    /**
     * 根据用户ID查询审计日志（分页）
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 审计日志分页列表
     */
    @Select("SELECT * FROM audit_logs WHERE user_id = #{userId} ORDER BY created_at DESC")
    IPage<AuditLog> findByUserId(Page<AuditLog> page, @Param("userId") Long userId);

    /**
     * 根据操作类型查询审计日志
     *
     * @param action 操作类型
     * @return 审计日志列表
     */
    @Select("SELECT * FROM audit_logs WHERE action = #{action} ORDER BY created_at DESC")
    List<AuditLog> findByAction(String action);

    /**
     * 根据资源类型和资源ID查询审计日志
     *
     * @param resourceType 资源类型
     * @param resourceId 资源ID
     * @return 审计日志列表
     */
    @Select("SELECT * FROM audit_logs WHERE resource_type = #{resourceType} AND resource_id = #{resourceId} ORDER BY created_at DESC")
    List<AuditLog> findByResource(@Param("resourceType") String resourceType, @Param("resourceId") Long resourceId);

    /**
     * 根据时间范围查询审计日志
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    @Select("SELECT * FROM audit_logs WHERE created_at >= #{startTime} AND created_at <= #{endTime} ORDER BY created_at DESC")
    List<AuditLog> findByTimeRange(@Param("startTime") LocalDateTime startTime,
                                    @Param("endTime") LocalDateTime endTime);

    /**
     * 根据用户ID和时间范围查询审计日志
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 审计日志列表
     */
    @Select("SELECT * FROM audit_logs WHERE user_id = #{userId} " +
            "AND created_at >= #{startTime} AND created_at <= #{endTime} " +
            "ORDER BY created_at DESC")
    List<AuditLog> findByUserIdAndTimeRange(@Param("userId") Long userId,
                                             @Param("startTime") LocalDateTime startTime,
                                             @Param("endTime") LocalDateTime endTime);
}
