package com.fitness.interceptor;

import com.fitness.annotation.CurrentUser;
import com.fitness.entity.User;
import com.fitness.common.utils.SecurityUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * CurrentUser 注解解析器
 */
@Component
public class CurrentUserArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CurrentUser.class)
                && parameter.getParameterType().equals(User.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) {
        // 从 SecurityUtils 获取 LoginUser
        com.fitness.common.core.domain.model.LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            return null;
        }
        // 将 LoginUser 转换为 User
        User user = new User();
        user.setId(loginUser.getUserId());
        user.setUsername(loginUser.getUsername());
        user.setEmail(loginUser.getEmail());
        user.setPhone(loginUser.getPhoneNumber());
        // 从LoginUser获取角色信息
        if (loginUser.getAuthorities() != null) {
            for (var authority : loginUser.getAuthorities()) {
                String authorityName = authority.getAuthority();
                if (authorityName.startsWith("ROLE_")) {
                    try {
                        user.setRole(com.fitness.entity.enums.UserRole.valueOf(authorityName.substring(5)));
                        break;
                    } catch (IllegalArgumentException e) {
                        // 忽略无效的角色
                    }
                }
            }
        }
        return user;
    }
}