package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.ChatRequest;
import com.fitness.dto.HealthAdviceDTO;
import com.fitness.dto.RecommendationDTO;
import com.fitness.entity.AiChatHistory;
import com.fitness.service.AIService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * AI服务控制器
 */
@Tag(name = "AI服务", description = "AI智能问答和推荐相关接口")
@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AIController {
    
    private final AIService aiService;
    
    @Operation(summary = "AI问答", description = "向AI助手提问健身相关问题")
    @PostMapping("/chat")
    public Result<String> chat(@Valid @RequestBody ChatRequest request) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        String answer = aiService.chat(userId, request.getQuestion());
        return Result.success(answer);
    }
    
    @Operation(summary = "获取个性化推荐", description = "基于健康数据获取课程和器材推荐")
    @GetMapping("/recommendations")
    public Result<RecommendationDTO> getRecommendations() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        RecommendationDTO recommendations = aiService.getRecommendations(studentId);
        return Result.success(recommendations);
    }
    
    @Operation(summary = "获取健康建议", description = "基于健康数据获取AI分析和建议")
    @GetMapping("/health-advice")
    public Result<HealthAdviceDTO> getHealthAdvice() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long studentId = loginUser.getUserId();
        HealthAdviceDTO advice = aiService.getHealthAdvice(studentId);
        return Result.success(advice);
    }
    
    @Operation(summary = "获取问答历史", description = "查看历史问答记录")
    @GetMapping("/chat-history")
    public Result<List<AiChatHistory>> getChatHistory() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();
        List<AiChatHistory> history = aiService.getChatHistory(userId);
        return Result.success(history);
    }
}
