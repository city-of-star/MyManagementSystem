package com.mms.usercenter.config;

import com.mms.usercenter.security.filter.JwtAuthenticationFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 实现功能【Spring Security 配置类】
 * <p>
 * 作用说明：
 * 1. 配置 Spring Security 的安全策略
 * 2. 与网关层配合：网关负责 JWT 验证，服务层负责加载用户权限信息
 * 3. 为方法级权限控制（@PreAuthorize）和 SecurityUtils 提供支持
 * <p>
 * 工作流程：
 * 1. 网关验证 JWT token，提取用户名，通过 Header 透传到服务层
 * 2. JwtAuthenticationFilter 从 Header 读取用户名
 * 3. 调用 UserDetailsService 加载用户详情和权限
 * 4. 设置到 SecurityContext，供后续权限验证使用
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:20
 */
@Configuration
@EnableWebSecurity  // 启用 Spring Security
@AllArgsConstructor
public class SecurityConfig {

    /**
     * 自定义 JWT 认证过滤器
     * 作用：从网关透传的 Header 中读取用户名，加载用户权限，设置到 SecurityContext
     */
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 配置安全过滤器链
     * <p>
     * 说明：
     * - 这是 Spring Security 的核心配置方法
     * - 定义了哪些路径需要认证，哪些路径可以放行
     * - 配置了自定义的 JWT 认证过滤器
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 禁用 CSRF 保护
                .csrf(AbstractHttpConfigurer::disable)
                // 配置 Session 策略为无状态
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 配置请求授权规则
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/auth/login",           // 登录
                                "/auth/refresh",         // 刷新
                                "/actuator/**",          // Spring Boot Actuator 端点
                                "/doc.html",             // Knife4j 主页面
                                "/v3/api-docs/**",       // OpenAPI 文档
                                "/webjars/**",           // Knife4j 静态资源
                                "/swagger-resources/**"  // Swagger 资源
                        ).permitAll()
                        
                        // 其他所有请求都需要认证
                        // 注意：这里说的"认证"不是验证 JWT（网关已做），而是要求 SecurityContext 中有用户信息
                        // 如果 SecurityContext 中没有用户信息，请求会被拒绝（返回 403）
                        .anyRequest().authenticated()
                )
                // 添加自定义 JWT 认证过滤器
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }

    /**
     * 密码编码器
     * <p>
     * 作用：用于登录时验证密码（BCrypt 加密）
     * 注意：虽然网关已经验证了 JWT，但登录接口仍然需要验证用户名和密码
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器
     * <p>
     * 作用：用于登录时的用户名密码认证
     * 注意：虽然当前主要使用 JWT 认证，但登录接口仍需要用户名密码认证
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}