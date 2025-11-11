package com.mms.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 实现功能【基础安全配置】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 禁用CSRF保护（主要用于防止跨站请求伪造）
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // 禁用HTTP Basic认证
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                // 禁用表单登录
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // 配置请求授权规则
                .authorizeExchange(authorize -> authorize
                        // 放行Actuator端点，用于监控和管理
                        .pathMatchers("/actuator/**").permitAll()
                        // 放行usercenter端点，用于登录和注册
                        .pathMatchers("/usercenter/auth/**").permitAll()
                        // 所有其他请求都需要鉴权
                        .anyExchange().authenticated()
                )
                .build();
    }
}

