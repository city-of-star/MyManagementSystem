package com.mms.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * 实现功能【TraceId 追踪过滤器】
 *
 * - 每个请求生成/提取 traceId
 * - 放入请求头并写入 MDC
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

	public static final String HEADER_TRACE_ID = "X-Trace-Id";
	public static final String MDC_TRACE_ID = "traceId";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String incomingTraceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
		String traceId = StringUtils.hasText(incomingTraceId) ? incomingTraceId : generateTraceId();

		MDC.put(MDC_TRACE_ID, traceId);

		ServerHttpRequest mutated = exchange.getRequest()
				.mutate()
				.header(HEADER_TRACE_ID, traceId)
				.build();

		return chain.filter(exchange.mutate().request(mutated).build())
				.doFinally(signalType -> MDC.remove(MDC_TRACE_ID));
	}

	private String generateTraceId() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}


