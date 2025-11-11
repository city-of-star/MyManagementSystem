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
import java.time.Duration;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GatewayExceptionHandler implements ErrorWebExceptionHandler {

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
	private static final String HEADER_TRACE_ID = "X-Trace-Id";
	private static final String MDC_TRACE_ID = "traceId";

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		ServerHttpResponse response = exchange.getResponse();
		response.getHeaders().setContentType(MediaType.APPLICATION_JSON);

		// 写入 traceId 到 MDC，便于 Response 中透出
		String traceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
		if (StringUtils.hasText(traceId)) {
			MDC.put(MDC_TRACE_ID, traceId);
		}

		HttpStatus status = resolveHttpStatus(ex);
		String message = resolveMessage(ex, status);
		response.setStatusCode(status);

		Response<Object> body = Response.error(status.value(), message);
		byte[] bytes = toJsonBytes(body);

		return response.writeWith(Mono.just(response.bufferFactory().wrap(bytes)))
				.doFinally(signalType -> MDC.remove(MDC_TRACE_ID));
	}

	private HttpStatus resolveHttpStatus(Throwable ex) {
		// 常见网关异常映射
		if (ex instanceof TimeoutException) {
			return HttpStatus.GATEWAY_TIMEOUT;
		}
		if (ex instanceof WebClientRequestException webClientEx) {
			// Reactor Netty 连接/读写失败
			if (webClientEx.getCause() instanceof TimeoutException) {
				return HttpStatus.GATEWAY_TIMEOUT;
			}
			if (webClientEx.getCause() instanceof ConnectException) {
				return HttpStatus.BAD_GATEWAY;
			}
			return HttpStatus.BAD_GATEWAY;
		}
		if (ex instanceof ConnectException) {
			return HttpStatus.BAD_GATEWAY;
		}
		if (ex instanceof ResponseStatusException rse) {
			HttpStatus status = rse.getStatusCode() instanceof HttpStatus httpStatus ? httpStatus : null;
			return status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR;
		}
		// Spring Cloud Gateway 下游不可达等
		String name = ex.getClass().getName();
		if (name.contains("NotFoundException")) {
			// org.springframework.cloud.gateway.support.NotFoundException
			return HttpStatus.SERVICE_UNAVAILABLE;
		}
		return HttpStatus.INTERNAL_SERVER_ERROR;
	}

	private String resolveMessage(Throwable ex, HttpStatus status) {
		if (status == HttpStatus.GATEWAY_TIMEOUT) {
			return "网关超时";
		}
		if (status == HttpStatus.BAD_GATEWAY) {
			return "网关连接下游服务失败";
		}
		if (status == HttpStatus.SERVICE_UNAVAILABLE) {
			return "服务不可用";
		}
		if (ex instanceof ResponseStatusException rse) {
			String reason = rse.getReason();
			if (StringUtils.hasText(reason)) {
				return reason;
			}
		}
		// 兜底
		return "网关内部错误";
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
}


