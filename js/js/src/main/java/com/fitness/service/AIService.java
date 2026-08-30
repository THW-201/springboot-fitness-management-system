package com.fitness.service;

import com.fitness.dto.HealthAdviceDTO;
import com.fitness.dto.RecommendationDTO;
import com.fitness.entity.AiChatHistory;

import java.util.List;

/**
 * AI服务接口
 */
public interface AIService {
    
    /**
     * AI问答
     * @param userId 用户ID
     * @param question 问题
     * @return 回答
     */
    String chat(Long userId, String question);
    
    /**
     * 获取个性化推荐
     * @param studentId 学生ID
     * @return 推荐结果
     */
    RecommendationDTO getRecommendations(Long studentId);
    
    /**
     * 获取健康建议
     * @param studentId 学生ID
     * @return 健康建议
     */
    HealthAdviceDTO getHealthAdvice(Long studentId);
    
    /**
     * 获取问答历史
     * @param userId 用户ID
     * @return 问答历史列表
     */
    List<AiChatHistory> getChatHistory(Long userId);
}
