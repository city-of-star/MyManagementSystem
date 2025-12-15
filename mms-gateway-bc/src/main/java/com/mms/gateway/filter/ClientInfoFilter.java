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
 * 从请求中提取客户端相关信息并放入请求头，透传到下游服务：
 * - 真实客户端IP（处理代理场景，放入 X-Client-Ip）
 * - 用户代理/浏览器信息（放入 X-User-Agent）
 * - 登录地点（基于IP地址解析，放入 X-Login-Location）
 * 所有请求都经过此过滤器（包括登录接口）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-02 11:29:49
 */
@Component
public class ClientInfoFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // 提取客户端真实IP
        String clientIp = getClientIp(request);
        
        // 提取用户代理（浏览器信息）
        String userAgent = getUserAgent(request);
        
        // 提取登录地点（通过IP解析，暂时为null，后续可接入IP解析服务）
        String loginLocation = getLoginLocation(clientIp);
        
        // 将信息放入请求头，透传到下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .header(GatewayConstants.Headers.CLIENT_IP, clientIp)
                .header(GatewayConstants.Headers.USER_AGENT, userAgent)
                .header(GatewayConstants.Headers.LOGIN_LOCATION, loginLocation)
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

    /**
     * 获取用户代理（浏览器信息）
     *
     * @param request 请求对象
     * @return 用户代理字符串，如果不存在则返回null
     */
    private String getUserAgent(ServerHttpRequest request) {
        String userAgent = request.getHeaders().getFirst("User-Agent");
        return StringUtils.hasText(userAgent) ? userAgent : null;
    }

    /**
     * 获取登录地点（通过IP地址解析）
     * <p>
     * 注意：IP地址解析需要第三方服务或IP地址库，这里暂时返回null
     * 后续可以接入IP解析服务（如：高德地图API、百度地图API、ip2region等）
     * </p>
     *
     * @param clientIp 客户端IP地址
     * @return 登录地点，如果无法解析则返回null
     */
    private String getLoginLocation(String clientIp) {
        // TODO: 接入IP地址解析服务
        // 可以使用：
        // 1. ip2region（本地IP地址库，速度快，但需要定期更新）
        // 2. 高德地图API（需要网络请求，有调用次数限制）
        // 3. 百度地图API（需要网络请求，有调用次数限制）
        // 4. 其他IP解析服务
        
        // 暂时返回空字符串，后续实现
        return "";
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行，在 JwtAuthFilter 之前执行
        return GatewayConstants.FilterOrder.CLIENT_IP_FILTER;
    }
}