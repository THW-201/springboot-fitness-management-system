package com.fitness.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fitness.common.ResultCode;
import com.fitness.dto.*;
import com.fitness.entity.CoachProfile;
import com.fitness.entity.StudentProfile;
import com.fitness.entity.User;
import com.fitness.entity.enums.UserRole;
import com.fitness.exception.BusinessException;
import com.fitness.mapper.CoachProfileMapper;
import com.fitness.mapper.StudentProfileMapper;
import com.fitness.mapper.UserMapper;
import com.fitness.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final StudentProfileMapper studentProfileMapper;
    private final CoachProfileMapper coachProfileMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Page<UserDTO> getUserList(Page<UserDTO> page, com.fitness.dto.UserQueryDTO query) {
        Page<User> userPage = new Page<>(page.getCurrent(), page.getSize());
        
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 角色筛选
        if (query != null && StringUtils.hasText(query.getRole())) {
            try {
                UserRole userRole = UserRole.valueOf(query.getRole().toUpperCase());
                queryWrapper.eq(User::getRole, userRole);
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的角色类型");
            }
        }
        
        // 关键词搜索（用户名、真实姓名、邮箱）
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, query.getKeyword())
                    .or()
                    .like(User::getRealName, query.getKeyword())
                    .or()
                    .like(User::getEmail, query.getKeyword())
            );
        }
        
        queryWrapper.orderByDesc(User::getCreatedAt);
        
        Page<User> result = userMapper.selectPage(userPage, queryWrapper);
        
        // 转换为DTO
        Page<UserDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        
        return dtoPage;
    }

    @Override
    public UserDTO getUserById(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        return convertToDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserDTO updateUser(Long userId, UpdateUserRequest request) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        
        // 更新基本信息
        if (StringUtils.hasText(request.getRealName())) {
            user.setRealName(request.getRealName());
        }
        
        if (StringUtils.hasText(request.getEmail())) {
            // 检查邮箱是否已被其他用户使用
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getEmail, request.getEmail())
                    .ne(User::getId, userId);
            if (userMapper.selectCount(queryWrapper) > 0) {
                throw new BusinessException(ResultCode.EMAIL_EXISTS, "该邮箱已被使用");
            }
            user.setEmail(request.getEmail());
        }
        
        if (StringUtils.hasText(request.getPhone())) {
            user.setPhone(request.getPhone());
        }
        
        if (StringUtils.hasText(request.getAvatarUrl())) {
            user.setAvatarUrl(request.getAvatarUrl());
        }
        
        // 更新密码
        if (StringUtils.hasText(request.getNewPassword())) {
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
            log.info("User {} password updated", userId);
        }
        
        userMapper.updateById(user);
        log.info("User {} information updated", userId);
        
        return convertToDTO(user);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteUser(Long userId) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new BusinessException(ResultCode.NOT_FOUND, "用户不存在");
        }
        
        // 不允许删除管理员账号（可选的安全措施）
        if (user.getRole() == UserRole.ADMIN) {
            throw new BusinessException(ResultCode.FORBIDDEN, "不允许删除管理员账号");
        }
        
        // 删除用户（级联删除会自动删除关联的profile）
        userMapper.deleteById(userId);
        log.info("User {} deleted", userId);
    }



    @Override
    public Page<UserDTO> getStudentsByCoachIdWithPage(Page page, UserQueryDTO query, Long coachId) {
        log.info("分页查询教练 {} 负责的学生列表: page={}, size={}, query={}", 
                coachId, page.getCurrent(), page.getSize(), query);
        
        // 查询该教练负责的所有学生ID
        List<StudentProfile> studentProfiles = studentProfileMapper.findByCoachId(coachId);
        List<Long> studentUserIds = studentProfiles.stream()
                .map(StudentProfile::getUserId)
                .collect(Collectors.toList());
        
        // 如果教练没有负责任何学生，返回空结果
        if (studentUserIds.isEmpty()) {
            return new Page<>(page.getCurrent(), page.getSize());
        }
        
        // 构建查询条件
        Page<User> userPage = new Page<>(page.getCurrent(), page.getSize());
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        
        // 只查询该教练负责的学生
        queryWrapper.in(User::getId, studentUserIds);
        queryWrapper.eq(User::getRole, UserRole.STUDENT);
        
        // 关键词搜索（用户名、真实姓名、邮箱）
        if (query != null && StringUtils.hasText(query.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                    .like(User::getUsername, query.getKeyword())
                    .or()
                    .like(User::getRealName, query.getKeyword())
                    .or()
                    .like(User::getEmail, query.getKeyword())
            );
        }
        
        queryWrapper.orderByDesc(User::getCreatedAt);
        
        // 执行分页查询
        Page<User> result = userMapper.selectPage(userPage, queryWrapper);
        
        // 转换为DTO
        Page<UserDTO> dtoPage = new Page<>(result.getCurrent(), result.getSize(), result.getTotal());
        List<UserDTO> dtoList = result.getRecords().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);
        
        log.info("查询到 {} 条学生记录", dtoPage.getTotal());
        return dtoPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignCoachToStudent(Long studentId, Long coachId) {
        // 验证学生存在
        User student = userMapper.selectById(studentId);
        if (student == null || student.getRole() != UserRole.STUDENT) {
            throw new BusinessException(ResultCode.NOT_FOUND, "学生不存在");
        }
        
        // 验证教练存在
        User coach = userMapper.selectById(coachId);
        if (coach == null || coach.getRole() != UserRole.COACH) {
            throw new BusinessException(ResultCode.NOT_FOUND, "教练不存在");
        }
        
        // 更新学生的教练ID
        StudentProfile studentProfile = studentProfileMapper.findByUserId(studentId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "学生信息不存在"));
        
        studentProfile.setCoachId(coachId);
        studentProfileMapper.updateById(studentProfile);
        
        log.info("Assigned coach {} to student {}", coachId, studentId);
    }
    
    @Override
    public com.fitness.dto.StudentProfileDTO getStudentProfile(Long userId) {
        StudentProfile profile = studentProfileMapper.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "学生档案不存在"));
        
        com.fitness.dto.StudentProfileDTO dto = new com.fitness.dto.StudentProfileDTO();
        BeanUtils.copyProperties(profile, dto);
        // 处理BigDecimal到Double的转换
        if (profile.getHeight() != null) {
            dto.setHeight(profile.getHeight().doubleValue());
        }
        if (profile.getWeight() != null) {
            dto.setWeight(profile.getWeight().doubleValue());
        }
        dto.setAvatarUrl(profile.getAvatarUrl());
        
        return dto;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public com.fitness.dto.StudentProfileDTO updateStudentProfile(Long userId, com.fitness.dto.StudentProfileDTO request) {
        log.info("开始更新学生档案，用户ID: {}, 请求数据: {}", userId, request);
        
        StudentProfile profile = studentProfileMapper.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ResultCode.NOT_FOUND, "学生档案不存在"));
        
        log.info("找到学生档案: {}", profile);
        
        // 更新字段
        if (request.getAge() != null) {
            profile.setAge(request.getAge());
            log.info("更新年龄: {}", request.getAge());
        }
        if (request.getHeight() != null) {
            profile.setHeight(java.math.BigDecimal.valueOf(request.getHeight()));
            log.info("更新身高: {}", request.getHeight());
        }
        if (request.getWeight() != null) {
            profile.setWeight(java.math.BigDecimal.valueOf(request.getWeight()));
            log.info("更新体重: {}", request.getWeight());
        }
        if (request.getGender() != null) {
            try {
                profile.setGender(com.fitness.entity.enums.Gender.valueOf(request.getGender()));
                log.info("更新性别: {}", request.getGender());
            } catch (IllegalArgumentException e) {
                throw new BusinessException(ResultCode.BAD_REQUEST, "无效的性别类型");
            }
        }
        if (request.getAvatarUrl() != null) {
            profile.setAvatarUrl(request.getAvatarUrl());
            log.info("更新头像URL: {}", request.getAvatarUrl());
        }
        
        int updateResult = studentProfileMapper.updateById(profile);
        log.info("更新结果: {}, 影响行数: {}", updateResult > 0 ? "成功" : "失败", updateResult);
        
        com.fitness.dto.StudentProfileDTO dto = new com.fitness.dto.StudentProfileDTO();
        BeanUtils.copyProperties(profile, dto);
        // 处理BigDecimal到Double的转换
        if (profile.getHeight() != null) {
            dto.setHeight(profile.getHeight().doubleValue());
        }
        if (profile.getWeight() != null) {
            dto.setWeight(profile.getWeight().doubleValue());
        }
        dto.setAvatarUrl(profile.getAvatarUrl());
        
        log.info("更新后的学生档案: {}", dto);
        return dto;
    }

    /**
     * 将User实体转换为UserDTO
     */
    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        BeanUtils.copyProperties(user, dto);
        
        // 手动设置role字段（ UserRole枚举转为String）
        if (user.getRole() != null) {
            dto.setRole(user.getRole().getCode());
        }
        
        // 根据角色加载额外信息
        if (user.getRole() == UserRole.STUDENT) {
            studentProfileMapper.findByUserId(user.getId()).ifPresent(profile -> {
                StudentProfileDTO studentDTO = new StudentProfileDTO();
                BeanUtils.copyProperties(profile, studentDTO);
                // 处理BigDecimal到Double的转换
                if (profile.getHeight() != null) {
                    studentDTO.setHeight(profile.getHeight().doubleValue());
                }
                if (profile.getWeight() != null) {
                    studentDTO.setWeight(profile.getWeight().doubleValue());
                }
                studentDTO.setAvatarUrl(profile.getAvatarUrl());
                dto.setStudentProfile(studentDTO);
            });
        } else if (user.getRole() == UserRole.COACH) {
            CoachProfile profile = coachProfileMapper.selectOne(Wrappers.lambdaQuery(CoachProfile.class).eq(CoachProfile::getUserId,user.getId()).last("limit 1"));
            dto.setCoachProfile(profile);
        }
        return dto;
    }
}
