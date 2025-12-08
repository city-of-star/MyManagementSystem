package com.mms.gateway.filter;

import com.mms.common.core.constants.gateway.GatewayConstants;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;

/**
 * 实现功能【客户端IP提取过滤器】
 * <p>
 * - 从请求中提取真实客户端IP（处理代理场景）
 * - 将IP放入请求头 X-Client-Ip，透传到下游服务
 * - 所有请求都经过此过滤器（包括登录接口）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-02 11:29:49
 */
@Component
public class ClientIpFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 提取客户端真实IP
        String clientIp = getClientIp(request);
        
        // 将IP放入请求头，透传到下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(GatewayConstants.Headers.CLIENT_IP, clientIp)
                .build();
        
        // 继续过滤器链
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 获取客户端真实IP
     * <p>
     * 优先级：
     * 1. X-Forwarded-For 请求头（第一个IP，处理代理场景）
     * 2. X-Real-IP 请求头
     * 3. RemoteAddress（直接连接的IP）
     * </p>
     *
     * @param request 请求对象
     * @return 客户端IP地址
     */
    private String getClientIp(ServerHttpRequest request) {
        // 优先从 X-Forwarded-For 获取（处理代理场景）
        String xForwardedFor = request.getHeaders().getFirst("X-Forwarded-For");
        if (StringUtils.hasText(xForwardedFor)) {
            // X-Forwarded-For 可能包含多个IP，取第一个（真实客户端IP）
            String[] ips = xForwardedFor.split(",");
            if (ips.length > 0) {
                String ip = ips[0].trim();
                if (StringUtils.hasText(ip) && !"unknown".equalsIgnoreCase(ip)) {
                    return ip;
                }
            }
        }
        
        // 从 X-Real-IP 获取
        String xRealIp = request.getHeaders().getFirst("X-Real-IP");
        if (StringUtils.hasText(xRealIp) && !"unknown".equalsIgnoreCase(xRealIp)) {
            return xRealIp;
        }
        
        // 从 RemoteAddress 获取（直接连接的IP）
        InetSocketAddress remoteAddress = request.getRemoteAddress();
        if (remoteAddress != null) {
            return remoteAddress.getAddress().getHostAddress();
        }
        
        // 如果都获取不到，返回未知
        return "unknown";
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行，在 JwtAuthFilter 之前执行
        return GatewayConstants.FilterOrder.CLIENT_IP_FILTER;
    }
}