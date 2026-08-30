package com.fitness.service;

import com.fitness.dto.*;

/**
 * 认证服务接口
 * 提供用户注册、登录、登出、Token 刷新等功能
 */
public interface AuthService {

    /**
     * 用户注册
     * 验证用户名/邮箱唯一性、密码加密、创建用户及对应的 Profile
     *
     * @param request 注册请求
     * @return 用户信息 DTO
     */
    UserDTO register(RegisterRequest request);

    /**
     * 用户登录
     * 验证凭据、生成 JWT Token
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    LoginResponse login(LoginRequest request);

    /**
     * 用户登出
     * 撤销 Token
     *
     * @param token JWT Token
     */
    void logout(String token);

    /**
     * 刷新 Token
     * 验证旧 Token 并生成新 Token
     *
     * @param token 旧 Token
     * @return 新的登录响应
     */
    LoginResponse refreshToken(String token);

    /**
     * 获取当前用户信息
     *
     * @param username 用户名
     * @return 用户信息 DTO
     */
    UserDTO getCurrentUser(String username);
}
