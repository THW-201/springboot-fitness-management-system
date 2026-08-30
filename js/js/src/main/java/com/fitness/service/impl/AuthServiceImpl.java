package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.fitness.common.ResultCode;
import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.common.utils.SecurityUtils;
import com.fitness.dto.*;
import com.fitness.entity.CoachProfile;
import com.fitness.entity.StudentProfile;
import com.fitness.entity.User;
import com.fitness.entity.enums.UserRole;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.CoachProfileMapper;
import com.fitness.mapper.StudentProfileMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.AuthService;
import com.fitness.service.TokenService;
import com.fitness.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * 认证服务实现类
 * 提供用户注册、登录、登出、Token 刷新等功能
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final CoachProfileMapper coachProfileMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final TokenService tokenService;
    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;

    /**
     * 用户注册
     * 验证用户名/邮箱唯一性、密码加密、创建用户及对应的 Profile
     *
     * @param request 注册请求
     * @return 用户信息 DTO
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO register(RegisterRequest request) {
        log.info("用户注册: username={}, email={}, role={}", request.getUsername(), request.getEmail(), request.getRole());

        // 验证用户名唯一性
        if (userMapper.existsByUsername(request.getUsername())) {
            log.warn("用户名已存在: {}", request.getUsername());
            throw new BusinessException(ResultCode.USERNAME_EXISTS, "用户名已存在");
        }

        // 验证邮箱唯一性
        if (userMapper.existsByEmail(request.getEmail())) {
            log.warn("邮箱已存在: {}", request.getEmail());
            throw new BusinessException(ResultCode.EMAIL_EXISTS, "邮箱已存在");
        }

        // 验证角色特定字段
        UserRole role = UserRole.valueOf(request.getRole());
        if (role == UserRole.STUDENT && (request.getStudentNumber() == null || request.getStudentNumber().trim().isEmpty())) {
            throw new BusinessException(ResultCode.BAD_REQUEST, "学生角色必须提供学号");
        }

        // 验证学号唯一性（学生角色）
        if (role == UserRole.STUDENT && studentProfileMapper.existsByStudentNumber(request.getStudentNumber())) {
            log.warn("学号已存在: {}", request.getStudentNumber());
            throw new BusinessException(ResultCode.BAD_REQUEST, "学号已存在");
        }

        // 创建用户
        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .email(request.getEmail())
                .phone(request.getPhone())
                .realName(request.getRealName())
                .role(role)
                .status(1) // 默认启用
                .build();

        userMapper.insert(user);
        log.info("用户创建成功: id={}, username={}", user.getId(), user.getUsername());

        // 根据角色创建对应的 Profile
        if (role == UserRole.STUDENT) {
            createStudentProfile(user.getId(), request);
        } else if (role == UserRole.COACH) {
            createCoachProfile(user.getId(), request);
        }

        return convertToUserDTO(user);
    }

    /**
     * 创建学生信息
     */
    private void createStudentProfile(Long userId, RegisterRequest request) {
        StudentProfile studentProfile = StudentProfile.builder()
                .userId(userId)
                .studentNumber(request.getStudentNumber())
                .coachId(request.getCoachId())
                .build();

        studentProfileMapper.insert(studentProfile);
        log.info("学生信息创建成功: userId={}, studentNumber={}", userId, request.getStudentNumber());
    }

    /**
     * 创建教练信息
     */
    private void createCoachProfile(Long userId, RegisterRequest request) {
        CoachProfile coachProfile = CoachProfile.builder()
                .userId(userId)
                .specialization(request.getSpecialization())
                .certification(request.getCertification())
                .experienceYears(request.getExperienceYears())
                .build();

        coachProfileMapper.insert(coachProfile);
        log.info("教练信息创建成功: userId={}", userId);
    }

    /**
     * 用户登录
     * 验证凭据、生成 JWT Token
     *
     * @param request 登录请求
     * @return 登录响应（包含 Token 和用户信息）
     */
    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("用户登录: username={}", request.getUsername());

        try {
            // 使用 Spring Security 进行身份验证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();

            // 生成 JWT Token
            String token = jwtTokenProvider.generateToken(userDetails);
            Long expiresIn = jwtTokenProvider.getExpiration();

            // 存储 Token 到 Redis
            tokenService.storeToken(userDetails.getUsername(), token, expiresIn);

            // 获取用户信息
            User user = userMapper.findByUsername(request.getUsername())
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));

            UserDTO userDTO = convertToUserDTO(user);

            log.info("用户登录成功: username={}, role={}", request.getUsername(), user.getRole());

            return LoginResponse.builder()
                    .accessToken(token)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn / 1000) // 转换为秒
                    .user(userDTO)
                    .build();

        } catch (BadCredentialsException e) {
            log.warn("用户登录失败: username={}, 原因: 用户名或密码错误", request.getUsername());
            throw new BusinessException(ResultCode.INVALID_CREDENTIALS, "用户名或密码错误");
        }
    }

    /**
     * 用户登出
     * 撤销 Token
     *
     * @param token JWT Token
     */
    @Override
    public void logout(String token) {
        try {
            String username = jwtTokenProvider.getUsernameFromToken(token);
            log.info("用户登出: username={}", username);

            // 计算 Token 剩余有效时间
            long remainingTime = jwtTokenProvider.getExpirationDateFromToken(token).getTime() - System.currentTimeMillis();
            if (remainingTime > 0) {
                tokenService.revokeToken(username, token, remainingTime);
                log.info("Token 已撤销: username={}", username);
            }
        } catch (Exception e) {
            log.error("登出失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "登出失败");
        }
    }

    /**
     * 刷新 Token
     * 验证旧 Token 并生成新 Token
     *
     * @param token 旧 Token
     * @return 新的登录响应
     */
    @Override
    public LoginResponse refreshToken(String token) {
        try {
            // 验证 Token
            if (!jwtTokenProvider.validateToken(token)) {
                throw new BusinessException(ResultCode.TOKEN_INVALID, "Token 无效或已过期");
            }

            String username = jwtTokenProvider.getUsernameFromToken(token);
            log.info("刷新 Token: username={}", username);

            // 验证 Token 是否在白名单中
            if (!tokenService.isTokenValid(username, token)) {
                throw new BusinessException(ResultCode.TOKEN_INVALID, "Token 已被撤销");
            }

            // 加载用户信息
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 生成新 Token
            String newToken = jwtTokenProvider.generateToken(userDetails);
            Long expiresIn = jwtTokenProvider.getExpiration();

            // 刷新 Token（撤销旧 Token，存储新 Token）
            tokenService.refreshToken(username, token, newToken, expiresIn);

            // 获取用户信息
            User user = userMapper.findByUsername(username)
                    .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "用户不存在"));

            UserDTO userDTO = convertToUserDTO(user);

            log.info("Token 刷新成功: username={}", username);

            return LoginResponse.builder()
                    .accessToken(newToken)
                    .tokenType("Bearer")
                    .expiresIn(expiresIn / 1000) // 转换为秒
                    .user(userDTO)
                    .build();

        } catch (Exception e) {
            log.error("Token 刷新失败: {}", e.getMessage());
            throw new BusinessException(ResultCode.INTERNAL_SERVER_ERROR, "Token 刷新失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     *
     * @param username 用户名
     * @return 用户信息 DTO
     */
    @Override
    public UserDTO getCurrentUser(String username) {
        log.debug("获取当前用户信息: username={}", username);

        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new BusinessException(ResultCode.USERNAME_EXISTS, "用户不存在"));

        return convertToUserDTO(user);
    }

    /**
     * 转换 User 实体为 UserDTO
     * 不包含密码字段，根据角色加载对应的 Profile
     */
    private UserDTO convertToUserDTO(User user) {
        UserDTO userDTO = UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .phone(user.getPhone())
                .realName(user.getRealName())
                .role(user.getRole().getCode())
                .avatarUrl(user.getAvatarUrl())
                .status(user.getStatus())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();

        // 根据角色加载对应的 Profile
        if (user.getRole() == UserRole.STUDENT) {
            studentProfileMapper.findByUserId(user.getId()).ifPresent(profile -> {
                StudentProfileDTO profileDTO = convertToStudentProfileDTO(profile);
                // 如果有教练，加载教练姓名
                if (profile.getCoachId() != null) {
                    User coach = userMapper.selectById(profile.getCoachId());
                    if (coach != null) {
                        profileDTO.setCoachName(coach.getRealName() != null ? coach.getRealName() : coach.getUsername());
                    }
                }
                userDTO.setStudentProfile(profileDTO);
            });
        } else if (user.getRole() == UserRole.COACH) {
            CoachProfile profile = coachProfileMapper.selectOne(Wrappers.lambdaQuery(CoachProfile.class).eq(CoachProfile::getUserId,user.getId()).last("limit 1"));
            userDTO.setCoachProfile(profile);
        }

        return userDTO;
    }

    /**
     * 转换 StudentProfile 实体为 StudentProfileDTO
     */
    private StudentProfileDTO convertToStudentProfileDTO(StudentProfile profile) {
        return StudentProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .studentNumber(profile.getStudentNumber())
                .coachId(profile.getCoachId())
                .gender(profile.getGender() != null ? profile.getGender().name() : null)
                .age(profile.getAge())
                .height(profile.getHeight() != null ? profile.getHeight().doubleValue() : null)
                .weight(profile.getWeight() != null ? profile.getWeight().doubleValue() : null)
                .build();
    }

    /**
     * 转换 CoachProfile 实体为 CoachProfileDTO
     */
    private CoachProfileDTO convertToCoachProfileDTO(CoachProfile profile) {
        return CoachProfileDTO.builder()
                .id(profile.getId())
                .userId(profile.getUserId())
                .specialization(profile.getSpecialization())
                .certification(profile.getCertification())
                .experienceYears(profile.getExperienceYears())
                .bio(profile.getBio())
                .avatarUrl(profile.getAvatarUrl())
                .introImages(profile.getIntroImages())
                .build();
    }
}