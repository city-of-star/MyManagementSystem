package com.mms.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 实现功能【JWT 鉴权过滤器】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Component
public class JwtAuthenticationFilter implements GlobalFilter, Ordered {

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        // 获取当前请求对象
        ServerHttpRequest request = exchange.getRequest();

        // 记录调试日志，显示当前请求路径（实际生产环境可能移除或调整日志级别）
        log.debug("JWT filter placeholder, request path: {}", request.getURI().getPath());

        // 目前直接放行请求，后续将在此处添加JWT令牌验证逻辑
        // 例如：检查请求头中的Authorization字段，验证JWT令牌有效性等
        // 继续执行过滤器链中的下一个过滤器
        return chain.filter(exchange);

    }

    @Override
    public int getOrder() {

        // 设置为较高优先级+20，确保在基础过滤器之后、业务逻辑之前执行
        // 后续可根据需要调整，可能在白名单过滤器、限流过滤器之后执行
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}

