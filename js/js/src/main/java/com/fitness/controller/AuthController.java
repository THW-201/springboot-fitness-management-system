package com.fitness.controller;

import com.fitness.common.Result;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.*;
import com.fitness.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供用户注册、登录、登出、Token 刷新、获取当前用户信息等接口
 */
@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "认证管理", description = "用户认证相关接口，包括注册、登录、登出、Token 刷新等")
public class AuthController {

    private final AuthService authService;

    /**
     * 用户注册
     *
     * @param request 注册请求
     * @return 用户信息
     */
    @PostMapping("/register")
    @Operation(
            summary = "用户注册",
            description = "注册新用户账号，支持管理员、教练、学生三种角色。学生角色必须提供学号，教练角色可选填专业信息。"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "注册成功",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "请求参数错误或用户名/邮箱/学号已存在",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<UserDTO> register(
            @Parameter(description = "注册请求信息", required = true)
            @Valid @RequestBody RegisterRequest request
    ) {
        log.info("收到注册请求: username={}, email={}, role={}", request.getUsername(), request.getEmail(), request.getRole());
        UserDTO userDTO = authService.register(request);
        return Result.success(userDTO);
    }

    /**
     * 用户登录
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    @PostMapping("/login")
    @Operation(
            summary = "用户登录",
            description = "使用用户名和密码登录系统，成功后返回 JWT Token 和用户信息"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "登录成功",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "用户名或密码错误",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<LoginResponse> login(
            @Parameter(description = "登录请求信息", required = true)
            @Valid @RequestBody LoginRequest request
    ) {
        log.info("收到登录请求: username={}", request.getUsername());
        LoginResponse response = authService.login(request);
        System.out.println("token:"+response.getAccessToken());
        return Result.success(response);
    }

    /**
     * 用户登出
     *
     * @return 成功消息
     */
    @PostMapping("/logout")
    @Operation(
            summary = "用户登出",
            description = "登出当前用户，撤销 Token。需要在请求头中携带有效的 JWT Token。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "登出成功",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "权限不足",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<String> logout(@RequestHeader("Authorization") String authorization) {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();
        log.info("收到登出请求: username={}", username);
        
        // 从 Authorization 头中提取 token
        String token = extractToken(authorization);
        authService.logout(token);
        return Result.success("登出成功");
    }
    /**
     * 刷新 Token
     *
     * @param request 刷新 Token 请求
     * @return 新的登录响应
     */
    @PostMapping("/refresh")
    @Operation(
            summary = "刷新 Token",
            description = "使用当前 Token 刷新获取新的 Token，延长登录有效期"
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "刷新成功",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Token 无效或已过期",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<LoginResponse> refreshToken(
            @Parameter(description = "刷新 Token 请求", required = true)
            @Valid @RequestBody RefreshTokenRequest request
    ) {
        log.info("收到刷新 Token 请求");
        LoginResponse response = authService.refreshToken(request.getToken());
        return Result.success(response);
    }

    /**
     * 获取当前用户信息
     *
     * @return 当前用户信息
     */
    @GetMapping("/me")
    @Operation(
            summary = "获取当前用户信息",
            description = "获取当前登录用户的详细信息，包括角色对应的 Profile 信息。需要在请求头中携带有效的 JWT Token。",
            security = @SecurityRequirement(name = "bearerAuth")
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "获取成功",
                    content = @Content(schema = @Schema(implementation = UserDTO.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "未授权或 Token 无效",
                    content = @Content(schema = @Schema(implementation = Result.class))
            )
    })
    public Result<UserDTO> getCurrentUser() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            log.warn("获取当前用户信息失败：用户未登录");
            throw new RuntimeException("用户未登录");
        }
        String username = loginUser.getUsername();
        log.info("获取当前用户信息: username={}", username);
        UserDTO userDTO = authService.getCurrentUser(username);
        return Result.success(userDTO);
    }

    /**
     * 从 Authorization 头中提取 Token
     *
     * @param authorization Authorization 头
     * @return Token 字符串
     */
    private String extractToken(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.substring(7);
        }
        throw new IllegalArgumentException("无效的 Authorization 头");
    }
}
