package com.mms.gateway.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.web.response.Response;
import com.mms.common.security.jwt.JwtUtil;
import io.jsonwebtoken.Claims;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
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
 *
 * - 支持白名单放行
 * - 校验 Authorization: Bearer <token>
 * - 解析用户信息并透传到下游
 * - 未认证/无效时返回标准响应体（带 traceId）
 */
@Component
public class JwtAuthFilter implements GlobalFilter, Ordered {

    // 日志记录器
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    // 透传的请求头
    public static final String HEADER_AUTHORIZATION = HttpHeaders.AUTHORIZATION;
    public static final String HEADER_TRACE_ID = "X-Trace-Id";
    public static final String HEADER_USER_NAME = "X-User-Name";

    // 白名单路径（网关层面不做鉴权）
    private static final List<String> WHITELIST = Arrays.asList(
            "/actuator/**",
            "/usercenter/auth/**",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Autowired(required = false)
    private JwtUtil jwtUtil;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String path = request.getURI().getPath();

        if (isWhitelisted(path)) {
            return chain.filter(exchange);
        }

        if (jwtUtil == null) {
            log.error("JWT 未配置，拒绝访问");
            return writeError(exchange, HttpStatus.INTERNAL_SERVER_ERROR, "网关JWT配置缺失");
        }

        String authHeader = request.getHeaders().getFirst(HEADER_AUTHORIZATION);
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "未认证");
        }

        String token = authHeader.substring("Bearer ".length()).trim();
        if (!jwtUtil.validateToken(token)) {
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "令牌无效或已过期");
        }

        Claims claims = jwtUtil.parseToken(token);
        String username = Optional.ofNullable(claims.get("username"))
                .map(Object::toString)
                .orElse(null);

        // 将用户信息透传到下游服务
        ServerHttpRequest mutatedRequest = request.mutate()
                .headers(httpHeaders -> {
                    if (StringUtils.hasText(username)) {
                        httpHeaders.set(HEADER_USER_NAME, username);
                    }
                })
                .build();

        return chain.filter(exchange.mutate().request(mutatedRequest).build());
    }

    private boolean isWhitelisted(String path) {
        for (String pattern : WHITELIST) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 确保 traceId 写入 MDC 以便 Response 序列化包含
        String traceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            MDC.put("traceId", traceId);
        }

        Response<Object> body = Response.error(status.value(), message);
        byte[] bytes = toJsonBytes(body);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);
        return response.writeWith(Mono.just(buffer))
                .doFinally(signalType -> MDC.remove("traceId"));
    }

    private byte[] toJsonBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            String fallback = "{\"code\":" + HttpStatus.INTERNAL_SERVER_ERROR.value() +
                    ",\"message\":\"响应序列化失败\",\"data\":null}";
            return fallback.getBytes(StandardCharsets.UTF_8);
        }
    }

    @Override
    public int getOrder() {
        // 在 TraceFilter 之后执行，保证已生成 traceId
        return Ordered.HIGHEST_PRECEDENCE + 20;
    }
}

