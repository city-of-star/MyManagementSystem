package com.mms.gateway.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mms.common.web.response.Response;
import org.slf4j.MDC;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.ConnectException;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeoutException;

/**
 * 实现功能【网关全局异常处理器 - WebFlux】
 * <p>
 * - 仅作用于 Spring Cloud Gateway（WebFlux），不影响各业务服务的 MVC 全局异常处理
 * - 统一返回 common 的 Response 结构，并携带 traceId
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-11 20:41:36
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 最高优先级，确保最先处理异常
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

	// JSON 序列化工具
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	// 追踪 ID 的请求头名称
	private static final String HEADER_TRACE_ID = "X-Trace-Id";
	// MDC 中存储追踪 ID 的键名
	private static final String MDC_TRACE_ID = "traceId";

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		// 设置响应内容类型为 JSON
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		// 写入 traceId 到 MDC，便于 Response 中透出
		// 从请求头中获取追踪 ID
		String traceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
		if (StringUtils.hasText(traceId)) {
			// 将追踪 ID 放入 MDC（Mapped Diagnostic Context），便于日志记录
			MDC.put(MDC_TRACE_ID, traceId);
		}

		// 根据异常类型解析对应的 HTTP 状态码
		HttpStatus status = resolveHttpStatus(ex);
		// 根据异常和状态码解析用户友好的错误消息
		String message = resolveMessage(ex, status);
		// 设置 HTTP 状态码
		response.setStatusCode(status);

		// 构建统一的错误响应体
		Response<Object> body = Response.error(status.value(), message);
		// 将响应体序列化为字节数组
		byte[] bytes = toJsonBytes(body);

		// 写入响应并清理 MDC
		return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)))
				.doFinally(signalType -> MDC.remove(MDC_TRACE_ID)); // 确保清理，避免内存泄漏
	}

	/**
	 * 根据异常类型解析对应的 HTTP 状态码
	 */
	private HttpStatus resolveHttpStatus(Throwable ex) {
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
	 */
	private String resolveMessage(Throwable ex, HttpStatus status) {
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
	 * 将对象序列化为 JSON 字节数组
	 * 如果序列化失败，返回兜底的错误响应
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
}


