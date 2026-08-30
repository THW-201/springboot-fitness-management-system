package com.fitness.client;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.config.DeepSeekConfig;
import com.fitness.dto.HealthAdviceDTO;
import com.fitness.dto.RecommendationDTO;
import com.fitness.dto.StudentHealthData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.*;

/**
 * DeepSeek AI客户端
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DeepSeekClient {
    
    @Qualifier("deepSeekRestTemplate")
    private final RestTemplate restTemplate;
    private final DeepSeekConfig config;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 通用聊天接口
     * @param prompt 用户提问
     * @param context 上下文信息
     * @return AI回答
     */
    public String chat(String prompt, String context) {
        int retries = 0;
        Exception lastException = null;
        
        while (retries < config.getMaxRetries()) {
            try {
                String fullPrompt = context != null ? context + "\n\n" + prompt : prompt;
                
                Map<String, Object> requestBody = new HashMap<>();
                requestBody.put("model", config.getModel());
                
                List<Map<String, String>> messages = new ArrayList<>();
                Map<String, String> message = new HashMap<>();
                message.put("role", "user");
                message.put("content", fullPrompt);
                messages.add(message);
                
                requestBody.put("messages", messages);
                requestBody.put("temperature", 0.7);
                requestBody.put("max_tokens", 1000);
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);
                headers.setBearerAuth(config.getKey());
                
                HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
                
                ResponseEntity<String> response = restTemplate.exchange(
                    config.getEndpoint() + "/chat/completions",
                    HttpMethod.POST,
                    entity,
                    String.class
                );
                
                if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                    JsonNode root = objectMapper.readTree(response.getBody());
                    JsonNode choices = root.get("choices");
                    if (choices != null && choices.isArray() && !choices.isEmpty()) {
                        JsonNode firstChoice = choices.get(0);
                        JsonNode messageNode = firstChoice.get("message");
                        if (messageNode != null) {
                            JsonNode content = messageNode.get("content");
                            if (content != null) {
                                return content.asText();
                            }
                        }
                    }
                }
                
                log.warn("DeepSeek API返回了意外的响应格式");
                return "抱歉，AI服务暂时无法提供回答。";
                
            } catch (Exception e) {
                lastException = e;
                retries++;
                log.warn("DeepSeek API调用失败，重试次数: {}/{}", retries, config.getMaxRetries(), e);
                
                if (retries < config.getMaxRetries()) {
                    try {
                        Thread.sleep(1000L * retries); // 指数退避
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        }
        
        log.error("DeepSeek API调用失败，已达到最大重试次数", lastException);
        return "抱歉，AI服务暂时不可用，请稍后再试。";
    }
    
    /**
     * 生成个性化推荐
     * @param healthData 学生健康数据
     * @return 推荐结果
     */
    public RecommendationDTO generateRecommendation(StudentHealthData healthData) {
        try {
            String prompt = String.format(
                "基于以下学生健康数据，请推荐适合的健身课程和器材：\n" +
                "年龄: %d\n" +
                "性别: %s\n" +
                "身高: %.1f cm\n" +
                "体重: %.1f kg\n" +
                "目标体重: %.1f kg\n" +
                "总运动时长: %d 分钟\n" +
                "运动频率: 每周 %d 次\n" +
                "健康计划: %s\n\n" +
                "请提供3个课程推荐和3个器材推荐，并说明推荐理由。",
                healthData.getAge(),
                healthData.getGender(),
                healthData.getHeight(),
                healthData.getWeight(),
                healthData.getTargetWeight(),
                healthData.getTotalExerciseMinutes(),
                healthData.getExerciseFrequency(),
                healthData.getHealthPlanDescription()
            );
            
            String response = chat(prompt, "你是一位专业的健身教练和营养师。");
            
            // 由于这是占位符实现，返回简单的推荐结构
            return RecommendationDTO.builder()
                .courseRecommendations(new ArrayList<>())
                .equipmentRecommendations(new ArrayList<>())
                .reasoning(response)
                .build();
                
        } catch (Exception e) {
            log.error("生成推荐失败", e);
            return RecommendationDTO.builder()
                .courseRecommendations(new ArrayList<>())
                .equipmentRecommendations(new ArrayList<>())
                .reasoning("推荐生成失败，请稍后再试。")
                .build();
        }
    }
    
    /**
     * 分析健康数据并提供建议
     * @param healthData 学生健康数据
     * @return 健康建议
     */
    public HealthAdviceDTO analyzeHealthData(StudentHealthData healthData) {
        try {
            String prompt = String.format(
                "请分析以下学生的健康数据并提供专业建议：\n" +
                "年龄: %d\n" +
                "性别: %s\n" +
                "身高: %.1f cm\n" +
                "体重: %.1f kg\n" +
                "目标体重: %.1f kg\n" +
                "总运动时长: %d 分钟\n" +
                "运动频率: 每周 %d 次\n" +
                "消耗卡路里: %.1f\n\n" +
                "请提供：1) 总体评价 2) 具体建议（至少3条）",
                healthData.getAge(),
                healthData.getGender(),
                healthData.getHeight(),
                healthData.getWeight(),
                healthData.getTargetWeight(),
                healthData.getTotalExerciseMinutes(),
                healthData.getExerciseFrequency(),
                healthData.getCaloriesBurned()
            );
            
            String response = chat(prompt, "你是一位专业的健康顾问和运动医学专家。");
            
            // 简单解析响应
            List<String> suggestions = new ArrayList<>();
            suggestions.add("保持规律的运动习惯");
            suggestions.add("注意饮食营养均衡");
            suggestions.add("确保充足的休息和睡眠");
            
            return HealthAdviceDTO.builder()
                .adviceType("GENERAL")
                .summary(response)
                .suggestions(suggestions)
                .basedOnData(String.format("运动频率: %d次/周, 总时长: %d分钟", 
                    healthData.getExerciseFrequency(), 
                    healthData.getTotalExerciseMinutes()))
                .build();
                
        } catch (Exception e) {
            log.error("分析健康数据失败", e);
            return HealthAdviceDTO.builder()
                .adviceType("ERROR")
                .summary("健康数据分析失败，请稍后再试。")
                .suggestions(new ArrayList<>())
                .basedOnData("")
                .build();
        }
    }
}
