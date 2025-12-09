package com.mms.usercenter.security.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.service.security.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
 * 从请求头提取用户信息（网关已透传）
 * 调用 UserDetailsService 加载用户权限
 * 创建 Authentication 对象
 * 放入 SecurityContext
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:40
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final UserDetailsServiceImpl userDetailsService;

    public JwtAuthenticationFilter(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 已存在认证则直接放行
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        // 从网关透传的 Header 读取用户名
        String username = request.getHeader(GatewayConstants.Headers.USER_NAME);
        if (!StringUtils.hasText(username)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 加载用户详情（含角色、权限），后续可做缓存优化
        SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(username);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());
        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }
}