package com.mms.gateway.utils;

import com.mms.common.web.response.Response;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * 实现功能【网关响应工具类】
 * <p>
 * 统一处理错误响应的写入逻辑，确保响应格式一致
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:45:28
 */
public class GatewayResponseUtils {

    /**
     * 写入错误响应
     * <p>
     * 自动处理 traceId 的提取和 MDC 的清理
     * </p>
     *
     * @param exchange 请求交换对象
     * @param status   HTTP 状态码
     * @param message  错误消息
     * @return Mono<Void>
     */
    public static Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 将 traceId 放入 MDC，便于统一响应结构（如日志/返回体）写回 traceId
        GatewayMdcUtils.putTraceIdFromRequest(exchange);

        // 标准错误响应体（与后端服务保持一致的 Response 结构）
        Response<Object> body = Response.error(status.value(), message);
        byte[] bytes = GatewayJsonUtils.toJsonBytes(body);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        // 写入响应并清理 MDC
        return response.writeWith(Mono.just(buffer))
                .doFinally(signalType -> GatewayMdcUtils.removeTraceId());
    }

    /**
     * 写入错误响应（带自定义响应体）
     *
     * @param exchange 请求交换对象
     * @param status   HTTP 状态码
     * @param body     响应体对象
     * @return Mono<Void>
     */
    public static Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, Response<?> body) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        // 将 traceId 放入 MDC
        GatewayMdcUtils.putTraceIdFromRequest(exchange);

        byte[] bytes = GatewayJsonUtils.toJsonBytes(body);
        DataBuffer buffer = response.bufferFactory().wrap(bytes);

        // 写入响应并清理 MDC
        return response.writeWith(Mono.just(buffer))
                .doFinally(signalType -> GatewayMdcUtils.removeTraceId());
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayResponseUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

