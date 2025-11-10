package com.mms.gateway.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * 实现功能【基础安全配置】
 * <p>
 * 暂时关闭默认登录表单与 HTTP Basic，放行所有请求，为后续接入 JWT 鉴权链预留扩展点。
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Configuration
@EnableWebFluxSecurity
public class GatewaySecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                // 禁用CSRF保护（主要用于防止跨站请求伪造）
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // 禁用HTTP Basic认证（弹出式登录框）
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                // 禁用表单登录（传统Web应用的登录页面）
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                // 配置请求授权规则
                .authorizeExchange(authorize -> authorize
                        // 放行Actuator端点，用于监控和管理
                        .pathMatchers("/actuator/**").permitAll()
                        // 暂时放行所有其他请求，后续可添加JWT鉴权
                        .anyExchange().permitAll()
                )
                .build();
    }
}

