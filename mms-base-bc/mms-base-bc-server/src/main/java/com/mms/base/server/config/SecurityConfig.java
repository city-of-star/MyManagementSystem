package com.mms.base.server.config;

import com.mms.base.server.security.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * 实现功能【安全配置】
 * <p>
 * - 无状态 Session
 * - 关闭 CSRF
 * - 公共文档、健康检查放行，其他请求要求已填充的 Authentication
 * - 注入下游侧的 JwtAuthenticationFilter 将权限写入 SecurityContext
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-23 20:06:37
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/actuator/**",          // Spring Boot Actuator 端点
                                "/doc.html",             // Knife4j 主页面
                                "/v3/api-docs/**",       // OpenAPI 文档
                                "/webjars/**",           // Knife4j 静态资源
                                "/swagger-resources/**", // Swagger 资源
                                "/favicon.ico"           // favicon 图标
                        ).permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

