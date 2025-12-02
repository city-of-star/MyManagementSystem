package com.mms.gateway.filter;

import com.mms.common.security.jwt.JwtUtil;
import com.mms.gateway.config.GatewayWhitelistConfig;
import com.mms.gateway.constants.GatewayConstants;
import com.mms.gateway.utils.GatewayResponseUtils;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Optional;

/**
 * 实现功能【JWT 鉴权过滤器】
 * <p>
 * - 支持白名单放行
 * - 校验 Authorization: Bearer <token>
 * - 解析用户信息并透传到下游
 * - 未认证/无效时返回标准响应体（带 traceId）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    // JWT 工具类
    @Resource
    private JwtUtil jwtUtil;

    // 白名单配置
    @Resource
    private GatewayWhitelistConfig whitelistConfig;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 白名单直接放行
        if (whitelistConfig.isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 读取 Authorization 头部
        String authHeader = request.getHeaders().getFirst(GatewayConstants.Headers.AUTHORIZATION);
        // 检查 Authorization 头是否存在且格式正确
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith(GatewayConstants.Headers.BEARER_PREFIX)) {
            return GatewayResponseUtils.writeError(exchange, HttpStatus.UNAUTHORIZED, "请求未携带认证信息，请检查Authorization请求头是否存在");
        }

        // 提取 JWT Token
        String token = authHeader.substring(GatewayConstants.Headers.BEARER_PREFIX.length()).trim();
        // 验证 Token 有效性
        if (!jwtUtil.validateToken(token)) {
            return GatewayResponseUtils.writeError(exchange, HttpStatus.UNAUTHORIZED, "身份验证已失效，请重新登录");
        }

        // 解析 Token，提取用户关键信息
        Claims claims = jwtUtil.parseToken(token);
        String username = Optional.ofNullable(claims.get("username"))
                .map(Object::toString)
                .orElse(null);

        // 将用户信息透传到下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .headers(httpHeaders -> {
                    if (StringUtils.hasText(username)) {
                        // 将用户名添加到请求头，供下游服务使用
                        httpHeaders.set(GatewayConstants.Headers.USER_NAME, username);
                    }
                })
                .build();

        // 继续过滤器链
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行，保证 traceId 已经生成并透传到请求头
        return GatewayConstants.FilterOrder.JWT_AUTH_FILTER;
    }
}

