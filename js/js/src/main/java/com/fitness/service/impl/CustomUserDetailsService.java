package com.fitness.service.impl;

import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.entity.User;
import com.fitness.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 自定义用户详情服务
 * 实现 Spring Security 的 UserDetailsService 接口
 * 从数据库加载用户信息并构建 UserDetails 对象
 * 
 * @author Fitness System
 * @version 1.0
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserMapper userMapper;

    /**
     * 根据用户名加载用户信息
     * 
     * @param username 用户名
     * @return UserDetails 对象
     * @throws UsernameNotFoundException 如果用户不存在
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("加载用户信息: {}", username);
        
        User user = userMapper.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("用户不存在: " + username));
        
        // 检查用户状态
        if (user.getStatus() == null || user.getStatus() == 0) {
            log.warn("用户已被禁用: {}", username);
            throw new UsernameNotFoundException("用户已被禁用: " + username);
        }
        
        log.debug("成功加载用户信息: {}, 角色: {}", username, user.getRole());
        
        return buildLoginUser(user);
    }

    /**
     * 构建 LoginUser 对象
     * 
     * @param user 用户实体
     * @return LoginUser 对象
     */
    private LoginUser buildLoginUser(User user) {
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(user.getId());
        loginUser.setUsername(user.getUsername());
        loginUser.setNickname(user.getRealName());
        loginUser.setEmail(user.getEmail());
        loginUser.setPhoneNumber(user.getPhone());
        loginUser.setAvatar(user.getAvatarUrl());
        loginUser.setUserType(user.getRole().getCode());
        loginUser.setPassword(user.getPassword());
        // 设置角色列表
        String role = "ROLE_" + user.getRole().getCode();
        loginUser.setRoles(Collections.singletonList(role));
        
        // 设置权限（可以根据需要扩展）
        loginUser.setPermissions(Collections.emptyList());
        
        return loginUser;
    }

    /**
     * 获取用户权限集合
     * 将用户角色转换为 Spring Security 的 GrantedAuthority
     * 
     * @param user 用户实体
     * @return 权限集合
     */
    private Collection<? extends GrantedAuthority> getAuthorities(User user) {
        // 角色需要添加 ROLE_ 前缀以符合 Spring Security 规范
        String role = "ROLE_" + user.getRole().getCode();
        return Collections.singletonList(new SimpleGrantedAuthority(role));
    }
}
