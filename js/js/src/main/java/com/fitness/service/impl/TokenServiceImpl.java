package com.fitness.service.impl;

import com.fitness.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Token 服务实现类
 * 使用 Redis 管理 JWT Token 的存储、验证和撤销
 * 
 * @author Fitness System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class TokenServiceImpl implements TokenService {

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String TOKEN_WHITELIST_PREFIX = "token:whitelist:";
    private static final String TOKEN_BLACKLIST_PREFIX = "token:blacklist:";

    /**
     * 存储 Token 到 Redis（白名单）
     * 
     * @param username 用户名
     * @param token JWT Token
     * @param expirationMs Token 过期时间（毫秒）
     */
    @Override
    public void storeToken(String username, String token, long expirationMs) {
        String key = TOKEN_WHITELIST_PREFIX + username;
        redisTemplate.opsForValue().set(key, token, expirationMs, TimeUnit.MILLISECONDS);
        log.info("Token 已存储到 Redis 白名单: username={}, expirationMs={}", username, expirationMs);
    }

    /**
     * 验证 Token 是否有效（在白名单中且未被撤销）
     * 
     * @param username 用户名
     * @param token JWT Token
     * @return true 如果 Token 有效，否则 false
     */
    @Override
    public boolean isTokenValid(String username, String token) {
        // 检查是否在黑名单中
        if (isTokenBlacklisted(token)) {
            log.debug("Token 在黑名单中: username={}", username);
            return false;
        }

        // 检查是否在白名单中
        String key = TOKEN_WHITELIST_PREFIX + username;
        Object storedToken = redisTemplate.opsForValue().get(key);
        
        boolean isValid = token.equals(storedToken);
        log.debug("Token 验证结果: username={}, isValid={}", username, isValid);
        
        return isValid;
    }

    /**
     * 撤销 Token（登出时调用）
     * 将 Token 从白名单移除并添加到黑名单
     * 
     * @param username 用户名
     * @param token JWT Token
     * @param remainingExpirationMs Token 剩余有效时间（毫秒）
     */
    @Override
    public void revokeToken(String username, String token, long remainingExpirationMs) {
        // 从白名单移除
        String whitelistKey = TOKEN_WHITELIST_PREFIX + username;
        redisTemplate.delete(whitelistKey);
        log.info("Token 已从白名单移除: username={}", username);

        // 添加到黑名单（设置过期时间为 Token 剩余有效时间）
        if (remainingExpirationMs > 0) {
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + token;
            redisTemplate.opsForValue().set(blacklistKey, "revoked", remainingExpirationMs, TimeUnit.MILLISECONDS);
            log.info("Token 已添加到黑名单: username={}, remainingExpirationMs={}", username, remainingExpirationMs);
        }
    }

    /**
     * 刷新 Token
     * 撤销旧 Token 并存储新 Token
     * 
     * @param username 用户名
     * @param oldToken 旧 Token
     * @param newToken 新 Token
     * @param expirationMs 新 Token 过期时间（毫秒）
     */
    @Override
    public void refreshToken(String username, String oldToken, String newToken, long expirationMs) {
        // 撤销旧 Token（添加到黑名单，剩余时间设为较短时间）
        if (oldToken != null && !oldToken.isEmpty()) {
            String blacklistKey = TOKEN_BLACKLIST_PREFIX + oldToken;
            // 旧 Token 在黑名单中保留 5 分钟，防止短时间内重复使用
            redisTemplate.opsForValue().set(blacklistKey, "revoked", 5, TimeUnit.MINUTES);
            log.info("旧 Token 已添加到黑名单: username={}", username);
        }

        // 存储新 Token 到白名单
        storeToken(username, newToken, expirationMs);
        log.info("Token 已刷新: username={}", username);
    }

    /**
     * 检查 Token 是否在黑名单中
     * 
     * @param token JWT Token
     * @return true 如果在黑名单中，否则 false
     */
    @Override
    public boolean isTokenBlacklisted(String token) {
        String key = TOKEN_BLACKLIST_PREFIX + token;
        Boolean exists = redisTemplate.hasKey(key);
        return Boolean.TRUE.equals(exists);
    }

    /**
     * 获取用户当前有效的 Token
     * 
     * @param username 用户名
     * @return Token 字符串，如果不存在则返回 null
     */
    @Override
    public String getCurrentToken(String username) {
        String key = TOKEN_WHITELIST_PREFIX + username;
        Object token = redisTemplate.opsForValue().get(key);
        return token != null ? token.toString() : null;
    }
}
