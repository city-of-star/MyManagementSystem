package com.mms.usercenter.security.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.service.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 实现功能【JWT 认证过滤器】
 * <p>
 * 作用说明：
 * 1. 从网关透传的 Header 中读取用户名（网关已验证 JWT token）
 * 2. 调用 UserDetailsService 加载用户详情和权限信息
 * 3. 创建 Authentication 对象并设置到 SecurityContext
 * 4. 为后续的方法级权限控制（@PreAuthorize）和 SecurityUtils 提供支持
 * <p>
 * 与网关的关系：
 * - 网关层（JwtAuthFilter）：验证 JWT token 有效性，提取用户名，透传到下游
 * - 服务层（本过滤器）：接收用户名，加载用户权限，设置到 SecurityContext
 * - 这是两层架构：网关做认证（Authentication），服务层做授权（Authorization）
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:40
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    /**
     * 过滤器核心逻辑
     * <p>
     * 执行流程：
     * 1. 检查 SecurityContext 中是否已有认证信息（避免重复处理）
     * 2. 从请求头读取网关透传的用户名
     * 3. 如果用户名存在，加载用户详情和权限
     * 4. 创建 Authentication 对象并设置到 SecurityContext
     * 5. 继续过滤器链
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 检查是否已有认证信息
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从网关透传的 Header 读取用户名
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        
        // 如果没有用户名说明网关已经放行，直接放行即可
        if (!StringUtils.hasText(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 加载用户详情和权限
        SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(username);

        // 创建 Authentication 对象
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        
        // 设置认证详情（IP 地址、Session ID 等）
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        // 设置到 SecurityContext
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 继续过滤器链
        filterChain.doFilter(request, response);
    }
}