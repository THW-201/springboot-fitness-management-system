package com.fitness.config;

import com.fitness.common.core.domain.model.LoginUser;
import com.fitness.service.TokenService;
import com.fitness.util.JwtTokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT 认证过滤器
 * 拦截所有 HTTP 请求，从请求头提取并验证 JWT Token
 * 将用户信息加载到 SecurityContext
 * 
 * @author Fitness System
 * @version 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final TokenService tokenService;

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * 执行过滤逻辑
     * 
     * @param request HTTP 请求
     * @param response HTTP 响应
     * @param filterChain 过滤器链
     * @throws ServletException Servlet 异常
     * @throws IOException IO 异常
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {
        
        try {
            // 从请求头提取 JWT Token
            String jwt = extractJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt)) {
                // 验证 Token 格式和签名
                if (jwtTokenProvider.validateToken(jwt)) {
                    // 从 Token 中提取用户名
                    String username = jwtTokenProvider.getUsernameFromToken(jwt);
                    
                    // 检查 Token 是否有效（在白名单中且未被撤销）
                    if (!tokenService.isTokenValid(username, jwt)) {
                        log.warn("Token 无效或已被撤销: username={}", username);
                        filterChain.doFilter(request, response);
                        return;
                    }
                    
                    // 如果用户名有效且当前 SecurityContext 中没有认证信息
                    if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                        // 加载用户详情（返回的是 LoginUser 对象）
                        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                        
                        // 再次验证 Token（包括用户名匹配）
                        if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                            // 创建认证对象（principal 直接使用 LoginUser）
                            UsernamePasswordAuthenticationToken authentication = 
                                    new UsernamePasswordAuthenticationToken(
                                            userDetails,  // 这里直接使用 LoginUser 对象
                                            null,
                                            userDetails.getAuthorities()
                                    );
                            
                            // 设置请求详情
                            authentication.setDetails(
                                    new WebAuthenticationDetailsSource().buildDetails(request)
                            );
                            
                            // 将认证信息设置到 SecurityContext
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                            
                            log.debug("用户 {} 认证成功", username);
                        }
                    }
                } else {
                    log.debug("Token 验证失败");
                }
            }
        } catch (Exception e) {
            log.error("无法设置用户认证: {}", e.getMessage());
        }
        
        // 继续过滤器链
        filterChain.doFilter(request, response);
    }

    /**
     * 从请求头中提取 JWT Token
     * 
     * @param request HTTP 请求
     * @return JWT Token 字符串，如果不存在则返回 null
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        
        return null;
    }
}
