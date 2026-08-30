package com.fitness.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitness.dto.*;
import com.fitness.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * AuthController 集成测试
 * 测试所有认证相关的 API 端点
 */
@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("认证控制器测试")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private RefreshTokenRequest refreshTokenRequest;
    private UserDTO userDTO;
    private LoginResponse loginResponse;

    @BeforeEach
    void setUp() {
        // 准备测试数据
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("testuser");
        registerRequest.setPassword("password123");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPhone("13800138000");
        registerRequest.setRealName("测试用户");
        registerRequest.setRole("STUDENT");
        registerRequest.setStudentNumber("2024001");

        loginRequest = new LoginRequest();
        loginRequest.setUsername("testuser");
        loginRequest.setPassword("password123");

        refreshTokenRequest = new RefreshTokenRequest();
        refreshTokenRequest.setToken("test.jwt.token");

        userDTO = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .phone("13800138000")
                .realName("测试用户")
                .role("STUDENT")
                .status(1)
                .build();

        loginResponse = LoginResponse.builder()
                .accessToken("test.jwt.token")
                .tokenType("Bearer")
                .expiresIn(3600L)
                .user(userDTO)
                .build();
    }

    @Test
    @DisplayName("测试用户注册 - 成功")
    void testRegister_Success() throws Exception {
        // Mock service
        when(authService.register(any(RegisterRequest.class))).thenReturn(userDTO);

        // 执行请求并验证
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    @DisplayName("测试用户注册 - 参数验证失败")
    void testRegister_ValidationFailed() throws Exception {
        // 创建无效的注册请求（缺少必填字段）
        RegisterRequest invalidRequest = new RegisterRequest();
        invalidRequest.setUsername(""); // 空用户名

        // 执行请求并验证
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试用户登录 - 成功")
    void testLogin_Success() throws Exception {
        // Mock service
        when(authService.login(any(LoginRequest.class))).thenReturn(loginResponse);

        // 执行请求并验证
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.accessToken").value("test.jwt.token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.data.expiresIn").value(3600))
                .andExpect(jsonPath("$.data.user.username").value("testuser"));
    }

    @Test
    @DisplayName("测试用户登录 - 参数验证失败")
    void testLogin_ValidationFailed() throws Exception {
        // 创建无效的登录请求
        LoginRequest invalidRequest = new LoginRequest();
        invalidRequest.setUsername(""); // 空用户名

        // 执行请求并验证
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试用户登出 - 成功")
    @WithMockUser(username = "testuser")
    void testLogout_Success() throws Exception {
        // Mock service
        doNothing().when(authService).logout(anyString());

        // 执行请求并验证
        mockMvc.perform(post("/auth/logout")
                        .header("Authorization", "Bearer test.jwt.token"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data").value("登出成功"));
    }

    @Test
    @DisplayName("测试用户登出 - 缺少 Authorization 头")
    @WithMockUser(username = "testuser")
    void testLogout_MissingAuthorizationHeader() throws Exception {
        // 执行请求并验证（不带 Authorization 头）
        mockMvc.perform(post("/auth/logout"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试刷新 Token - 成功")
    void testRefreshToken_Success() throws Exception {
        // Mock service
        when(authService.refreshToken(anyString())).thenReturn(loginResponse);

        // 执行请求并验证
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(refreshTokenRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.accessToken").value("test.jwt.token"))
                .andExpect(jsonPath("$.data.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("测试刷新 Token - 参数验证失败")
    void testRefreshToken_ValidationFailed() throws Exception {
        // 创建无效的刷新请求
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest();
        invalidRequest.setToken(""); // 空 Token

        // 执行请求并验证
        mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("测试获取当前用户信息 - 成功")
    @WithMockUser(username = "testuser")
    void testGetCurrentUser_Success() throws Exception {
        // Mock service
        when(authService.getCurrentUser(anyString())).thenReturn(userDTO);

        // 执行请求并验证
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("操作成功"))
                .andExpect(jsonPath("$.data.username").value("testuser"))
                .andExpect(jsonPath("$.data.email").value("test@example.com"))
                .andExpect(jsonPath("$.data.role").value("STUDENT"));
    }

    @Test
    @DisplayName("测试获取当前用户信息 - 未认证")
    void testGetCurrentUser_Unauthorized() throws Exception {
        // 执行请求并验证（未认证）
        mockMvc.perform(get("/auth/me"))
                .andExpect(status().isUnauthorized());
    }
}
