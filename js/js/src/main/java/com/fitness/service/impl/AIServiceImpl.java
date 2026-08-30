package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.fitness.client.DeepSeekClient;
import com.fitness.dto.*;
import com.fitness.entity.*;
import com.fitness.entity.enums.ReservationType;
import com.fitness.entity.enums.UserRole;
import com.fitness.mapper.*;
import com.fitness.service.AIService;
import com.fitness.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * AI服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIServiceImpl implements AIService {

    private final DeepSeekClient deepSeekClient;
    private final AiChatHistoryMapper chatHistoryMapper;
    private final HealthAdviceMapper healthAdviceMapper;
    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final HealthPlanMapper healthPlanMapper;
    private final CheckInMapper checkInMapper;
    private final ReservationMapper reservationMapper;
    private final CacheService cacheService;

    private static final String CACHE_PREFIX_RECOMMENDATION = "ai:recommendation:";
    private static final long CACHE_TTL_HOURS = 1;

    // 不适当内容过滤的简单正则表达式
    private static final Pattern INAPPROPRIATE_PATTERN = Pattern.compile(
            ".*(政治|暴力|色情|赌博|毒品).*",
            Pattern.CASE_INSENSITIVE
    );

    @Override
    public String chat(Long userId, String question) {
        long startTime = System.currentTimeMillis();

        try {
            // 过滤不适当的问题
            if (INAPPROPRIATE_PATTERN.matcher(question).matches()) {
                log.warn("用户 {} 提交了不适当的问题", userId);
                return "抱歉，您的问题包含不适当的内容，请重新提问。";
            }

            // 调用DeepSeek API
            String context = "你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容200个字左右";
            String answer = deepSeekClient.chat(question, context);

            long responseTime = System.currentTimeMillis() - startTime;

            // 记录问答历史
            AiChatHistory history = new AiChatHistory();
            history.setUserId(userId);
            history.setQuestion(question);
            history.setAnswer(answer);
            history.setContext(context);
            history.setResponseTimeMs((int) responseTime);
            history.setCreatedAt(LocalDateTime.now());

            chatHistoryMapper.insert(history);

            log.info("AI问答完成，用户: {}, 响应时间: {}ms", userId, responseTime);

            return answer;

        } catch (Exception e) {
            log.error("AI问答失败", e);
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }

    @Override
    public RecommendationDTO getRecommendations(Long studentId) {


        User user = userMapper.selectById(studentId);
        if (!user.getRole().equals(UserRole.STUDENT)) {
            String context = "你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容250个字左右";
            String answer = deepSeekClient.chat("根据一个正常普通人的身体情况进行健身、运动、营养方面的建议", context);
            RecommendationDTO recommendationDTO = new RecommendationDTO();
            recommendationDTO.setReasoning(answer);
            recommendationDTO.setCourseRecommendations(Arrays.asList());
            recommendationDTO.setEquipmentRecommendations(Arrays.asList());
            return recommendationDTO;
        }
        try {
            // 收集学生健康数据
            StudentHealthData healthData = collectStudentHealthData(studentId);
            if (healthData==null){
                String context = "你是一位专业的健身顾问，请回答用户关于健身、运动、营养等方面的问题，回答内容250个字左右";
                String answer = deepSeekClient.chat("根据一个正常普通人的身体情况进行健身、运动、营养方面的建议", context);
                RecommendationDTO recommendationDTO = new RecommendationDTO();
                recommendationDTO.setReasoning(answer);
                recommendationDTO.setCourseRecommendations(Arrays.asList());
                recommendationDTO.setEquipmentRecommendations(Arrays.asList());
                return recommendationDTO;
            }

            // 调用DeepSeek API生成推荐
            RecommendationDTO recommendation = deepSeekClient.generateRecommendation(healthData);


            log.info("生成推荐完成，学生: {}", studentId);

            return recommendation;

        } catch (Exception e) {
            log.error("生成推荐失败", e);
            return RecommendationDTO.builder()
                    .courseRecommendations(List.of())
                    .equipmentRecommendations(List.of())
                    .reasoning("推荐生成失败，请稍后再试。")
                    .build();
        }
    }

    @Override
    public HealthAdviceDTO getHealthAdvice(Long studentId) {
        try {
            // 收集学生健康数据
            StudentHealthData healthData = collectStudentHealthData(studentId);

            // 调用DeepSeek API分析健康数据
            HealthAdviceDTO advice = deepSeekClient.analyzeHealthData(healthData);

            // 保存健康建议到数据库
            HealthAdvice healthAdviceEntity = new HealthAdvice();
            healthAdviceEntity.setStudentId(studentId);
            healthAdviceEntity.setAdviceType(advice.getAdviceType());
            healthAdviceEntity.setContent(advice.getSummary());
            healthAdviceEntity.setBasedOnData(advice.getBasedOnData());
            healthAdviceEntity.setCreatedAt(LocalDateTime.now());

            healthAdviceMapper.insert(healthAdviceEntity);

            log.info("生成健康建议完成，学生: {}", studentId);

            return advice;

        } catch (Exception e) {
            log.error("生成健康建议失败", e);
            return HealthAdviceDTO.builder()
                    .adviceType("ERROR")
                    .summary("健康建议生成失败，请稍后再试。")
                    .suggestions(List.of())
                    .basedOnData("")
                    .build();
        }
    }

    @Override
    public List<AiChatHistory> getChatHistory(Long userId) {
        return chatHistoryMapper.selectList(
                new LambdaQueryWrapper<AiChatHistory>()
                        .eq(AiChatHistory::getUserId, userId)
                        .orderByDesc(AiChatHistory::getCreatedAt)
                        .last("LIMIT 50")
        );
    }

    /**
     * 收集学生健康数据
     */
    private StudentHealthData collectStudentHealthData(Long studentId) {
        // 获取学生基本信息
        StudentProfile profile = studentProfileMapper.selectOne(
                new LambdaQueryWrapper<StudentProfile>()
                        .eq(StudentProfile::getUserId, studentId)
        );
        if (profile==null){
            return null;
        }

        // 获取健康计划
        HealthPlan healthPlan = healthPlanMapper.selectOne(
                new LambdaQueryWrapper<HealthPlan>()
                        .eq(HealthPlan::getStudentId, studentId)
                        .eq(HealthPlan::getStatus, "ACTIVE")
                        .orderByDesc(HealthPlan::getCreatedAt)
                        .last("LIMIT 1")
        );

        // 统计运动数据
        List<CheckIn> checkIns = checkInMapper.selectList(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getStudentId, studentId)
        );

        int totalExerciseMinutes = 0;
        BigDecimal totalCalories = BigDecimal.ZERO;

        for (CheckIn checkIn : checkIns) {
            if (checkIn.getDurationMinutes() != null) {
                totalExerciseMinutes += checkIn.getDurationMinutes();
            }
            if (checkIn.getCaloriesBurned() != null) {
                totalCalories = totalCalories.add(checkIn.getCaloriesBurned());
            }
        }

        // 计算运动频率（最近30天）
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        Long recentCheckIns = checkInMapper.selectCount(
                new LambdaQueryWrapper<CheckIn>()
                        .eq(CheckIn::getStudentId, studentId)
                        .ge(CheckIn::getCheckInTime, thirtyDaysAgo)
        );

        int exerciseFrequency = (int) (recentCheckIns / 4); // 每周平均次数

        return StudentHealthData.builder()
                .studentId(studentId)

                // age
                .age(Optional.of(profile)
                        .map(StudentProfile::getAge)
                        .orElse(null))

                // gender
                .gender(Optional.ofNullable(profile)
                        .map(StudentProfile::getGender)
                        .map(Enum::name)
                        .orElse(null))

                // height
                .height(Optional.ofNullable(profile)
                        .map(StudentProfile::getHeight)
                        .orElse(null))

                // weight
                .weight(Optional.ofNullable(profile)
                        .map(StudentProfile::getWeight)
                        .orElse(null))

                // targetWeight
                .targetWeight(Optional.ofNullable(healthPlan)
                        .map(HealthPlan::getTargetWeight)
                        .orElse(null))

                .totalExerciseMinutes(totalExerciseMinutes)
                .exerciseFrequency(exerciseFrequency)
                .caloriesBurned(totalCalories)

                // healthPlanDescription
                .healthPlanDescription(Optional.ofNullable(healthPlan)
                        .map(HealthPlan::getDescription)
                        .orElse(null))

                .build();
    }
}
