package com.fitness.service;

/**
 * Token 服务接口
 * 管理 JWT Token 的存储、验证和撤销
 * 
 * @author Fitness System
 * @version 1.0
 */
public interface TokenService {

    /**
     * 存储 Token 到 Redis（白名单）
     * 
     * @param username 用户名
     * @param token JWT Token
     * @param expirationMs Token 过期时间（毫秒）
     */
    void storeToken(String username, String token, long expirationMs);

    /**
     * 验证 Token 是否有效（在白名单中且未被撤销）
     * 
     * @param username 用户名
     * @param token JWT Token
     * @return true 如果 Token 有效，否则 false
     */
    boolean isTokenValid(String username, String token);

    /**
     * 撤销 Token（登出时调用）
     * 将 Token 从白名单移除并添加到黑名单
     * 
     * @param username 用户名
     * @param token JWT Token
     * @param remainingExpirationMs Token 剩余有效时间（毫秒）
     */
    void revokeToken(String username, String token, long remainingExpirationMs);

    /**
     * 刷新 Token
     * 撤销旧 Token 并存储新 Token
     * 
     * @param username 用户名
     * @param oldToken 旧 Token
     * @param newToken 新 Token
     * @param expirationMs 新 Token 过期时间（毫秒）
     */
    void refreshToken(String username, String oldToken, String newToken, long expirationMs);

    /**
     * 检查 Token 是否在黑名单中
     * 
     * @param token JWT Token
     * @return true 如果在黑名单中，否则 false
     */
    boolean isTokenBlacklisted(String token);

    /**
     * 获取用户当前有效的 Token
     * 
     * @param username 用户名
     * @return Token 字符串，如果不存在则返回 null
     */
    String getCurrentToken(String username);
}
