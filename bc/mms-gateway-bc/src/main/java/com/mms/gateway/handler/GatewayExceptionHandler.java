package com.mms.gateway.handler;

import com.mms.gateway.util.GatewayExceptionUtil;
import com.mms.gateway.util.GatewayResponseUtil;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

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

	@Override
	public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
		// 根据异常类型解析对应的 HTTP 状态码
		HttpStatus status = GatewayExceptionUtil.resolveHttpStatus(ex);
		// 根据异常和状态码解析用户友好的错误消息
		String message = GatewayExceptionUtil.resolveMessage(ex, status);

		// 使用统一的响应工具写入错误响应
		return GatewayResponseUtil.writeError(exchange, status, message);
	}
}


