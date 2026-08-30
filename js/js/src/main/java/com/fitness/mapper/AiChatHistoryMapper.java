package com.fitness.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.entity.AiChatHistory;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;

/**
 * AI问答历史 Mapper 接口
 * 提供AI问答历史数据访问操作
 */
@Mapper
public interface AiChatHistoryMapper extends BaseMapper<AiChatHistory> {

    /**
     * 根据用户ID查询问答历史（分页）
     *
     * @param page 分页对象
     * @param userId 用户ID
     * @return 问答历史分页列表
     */
    @Select("SELECT * FROM ai_chat_history WHERE user_id = #{userId} ORDER BY created_at DESC")
    IPage<AiChatHistory> findByUserId(Page<AiChatHistory> page, @Param("userId") Long userId);

    /**
     * 根据用户ID查询最近的问答历史
     *
     * @param userId 用户ID
     * @param limit 限制数量
     * @return 问答历史列表
     */
    @Select("SELECT * FROM ai_chat_history WHERE user_id = #{userId} ORDER BY created_at DESC LIMIT #{limit}")
    List<AiChatHistory> findRecentByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 根据用户ID和时间范围查询问答历史
     *
     * @param userId 用户ID
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 问答历史列表
     */
    @Select("SELECT * FROM ai_chat_history WHERE user_id = #{userId} " +
            "AND created_at >= #{startTime} AND created_at <= #{endTime} " +
            "ORDER BY created_at DESC")
    List<AiChatHistory> findByUserIdAndTimeRange(@Param("userId") Long userId,
                                                  @Param("startTime") LocalDateTime startTime,
                                                  @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户的问答次数
     *
     * @param userId 用户ID
     * @return 问答次数
     */
    @Select("SELECT COUNT(*) FROM ai_chat_history WHERE user_id = #{userId}")
    Integer countByUserId(Long userId);
}
