package com.mms.gateway.utils;

import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;

import java.net.ConnectException;
import java.util.concurrent.TimeoutException;

/**
 * 实现功能【网关异常处理工具类】
 * <p>
 * 统一处理异常到 HTTP 状态码和错误消息的转换逻辑
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:36:27
 */
public class GatewayExceptionUtils {

    /**
     * 根据异常类型解析对应的 HTTP 状态码
     *
     * @param ex 异常对象
     * @return HTTP 状态码
     */
    public static HttpStatus resolveHttpStatus(Throwable ex) {
        // 超时异常 -> 504 Gateway Timeout
        if (ex instanceof TimeoutException) {
            return HttpStatus.GATEWAY_TIMEOUT;
        }
        // WebClient 请求异常（通常是下游服务调用问题）
        if (ex instanceof WebClientRequestException webClientEx) {
            // Reactor Netty 连接/读写失败
            if (webClientEx.getCause() instanceof TimeoutException) {
                return HttpStatus.GATEWAY_TIMEOUT;
            }
            if (webClientEx.getCause() instanceof ConnectException) {
                return HttpStatus.BAD_GATEWAY; // 502 Bad Gateway
            }
            return HttpStatus.BAD_GATEWAY;
        }
        // 连接异常 -> 502 Bad Gateway
        if (ex instanceof ConnectException) {
            return HttpStatus.BAD_GATEWAY;
        }
        // Spring 响应状态异常
        if (ex instanceof ResponseStatusException rse) {
            HttpStatus status = rse.getStatusCode() instanceof HttpStatus httpStatus ? httpStatus : null;
            return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
        }
        // Spring Cloud Gateway 下游服务不可达
        String name = ex.getClass().getName();
        if (name.contains("NotFoundException")) {
            // org.springframework.cloud.gateway.support.NotFoundException
            return HttpStatus.SERVICE_UNAVAILABLE; // 503 Service Unavailable
        }
        // 其他未知异常 -> 500 Internal Server Error
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /**
     * 根据异常和状态码解析用户友好的错误消息
     *
     * @param ex     异常对象
     * @param status HTTP 状态码
     * @return 错误消息
     */
    public static String resolveMessage(Throwable ex, HttpStatus status) {
        // 根据状态码返回对应的中文错误描述
        if (status == HttpStatus.GATEWAY_TIMEOUT) {
            return "网关超时";
        }
        if (status == HttpStatus.BAD_GATEWAY) {
            return "网关连接下游服务失败";
        }
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            return "服务不可用";
        }
        // 如果是 ResponseStatusException，使用其原始原因
        if (ex instanceof ResponseStatusException rse) {
            String reason = rse.getReason();
            if (StringUtils.hasText(reason)) {
                return reason;
            }
        }
        // 兜底错误消息
        return "网关内部错误";
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayExceptionUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

