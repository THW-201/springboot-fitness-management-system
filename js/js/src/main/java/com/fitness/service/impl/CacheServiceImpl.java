package com.fitness.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.service.CacheService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 缓存服务实现类
 * 基于Redis实现缓存操作
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CacheServiceImpl implements CacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    public <T> T get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            if (value == null) {
                return null;
            }
            
            // 如果是String类型，尝试反序列化
            if (value instanceof String) {
                return objectMapper.readValue((String) value, type);
            }
            
            // 直接类型转换
            return type.cast(value);
        } catch (JsonProcessingException e) {
            log.error("Failed to deserialize cache value for key: {}", key, e);
            return null;
        } catch (ClassCastException e) {
            log.error("Failed to cast cache value for key: {}", key, e);
            return null;
        }
    }

    @Override
    public void set(String key, Object value) {
        try {
            // 序列化为JSON字符串
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize cache value for key: {}", key, e);
        }
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        try {
            // 序列化为JSON字符串
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, timeout, unit);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize cache value for key: {}", key, e);
        }
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
        log.debug("Deleted cache key: {}", key);
    }

    @Override
    public void deletePattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.debug("Deleted {} cache keys matching pattern: {}", keys.size(), pattern);
        }
    }

    @Override
    public boolean exists(String key) {
        Boolean result = redisTemplate.hasKey(key);
        return result != null && result;
    }

    @Override
    public boolean expire(String key, long timeout, TimeUnit unit) {
        Boolean result = redisTemplate.expire(key, timeout, unit);
        return result != null && result;
    }

    @Override
    public long getExpire(String key, TimeUnit unit) {
        Long expire = redisTemplate.getExpire(key, unit);
        return expire != null ? expire : -2;
    }
}
