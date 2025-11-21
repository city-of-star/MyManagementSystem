package com.mms.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.web.response.Response;
import com.mms.common.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
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

    // 路径匹配器，用于白名单匹配
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    // JSON 序列化工具
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 透传的请求头常量定义
    public static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String HEADER_USER_NAME = "X-User-Name";

    // 白名单路径（网关层面不做鉴权）
    private static final List<String> WHITELIST = Arrays.asList(
            "/actuator/**",           // Spring Boot Actuator 端点
            "/usercenter/auth/**",    // 认证相关接口
            "/swagger-ui/**",         // Swagger UI
            "/v3/api-docs/**"         // OpenAPI 文档
    );

    // JWT 工具类
    @Resource
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        // 1) 白名单直接放行（无需校验 JWT）
        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        // 2) 读取并校验 Authorization 头部
        String authHeader = request.getHeaders().getFirst(HEADER_AUTHORIZATION);
        // 检查 Authorization 头是否存在且格式正确
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "未认证");
        }

        // 提取 JWT Token
        String token = authHeader.substring("Bearer ".length()).trim();
        // 验证 Token 有效性
        if (!jwtUtil.validateToken(token)) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "令牌无效或已过期");
        }

        // 3) 解析 Token，提取用户关键信息（示例：username）
        Claims claims = jwtUtil.parseToken(token);
        String username = Optional.ofNullable(claims.get("username"))
                .map(Object::toString)
                .orElse(null);

        // 4) 将用户信息透传到下游服务（通过自定义请求头），避免各服务重复解析 JWT
        ServerHttpRequest mutatedRequest = request.mutate()
                .headers(httpHeaders -> {
                    if (StringUtils.hasText(username)) {
                        // 将用户名添加到请求头，供下游服务使用
                        httpHeaders.set(HEADER_USER_NAME, username);
                    }
                })
                .build();

        // 继续过滤器链
        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    /**
     * 检查路径是否在白名单中
     */
    private boolean isWhitelisted(String path) {
        for (String pattern : WHITELIST) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 写入错误响应
     */
    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 将 traceId 放入 MDC，便于统一响应结构（如日志/返回体）写回 traceId
        String traceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            MDC.put("traceId", traceId);
        }

        // 标准错误响应体（与后端服务保持一致的 Response 结构）
        Response<Object> body = Response.error(status.value(), message);
        byte[] bytes = toJsonBytes(body);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        // 写入响应并清理 MDC
        return response.writeWith(Mono.just(buffer))
                .doFinally(signalType -> MDC.remove("traceId"));
    }

    /**
     * 对象序列化为 JSON 字节数组
     */
    private byte[] toJsonBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            // 序列化失败时的兜底响应
            String fallback = "{\"code\":" + HttpStatus.INTERNAL_SERVER_ERROR.value() +
                    ",\"message\":\"响应序列化失败\",\"data\":null}";
            return fallback.getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行，保证 traceId 已经生成并透传到请求头
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}

