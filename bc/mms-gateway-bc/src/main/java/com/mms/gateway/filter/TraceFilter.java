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
 * <p>
 * - 每个请求生成/提取 traceId
 * - 放入请求头并写入 MDC
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Component
public class TraceFilter implements GlobalFilter, Ordered {

	public static final String HEADER_TRACE_ID = "X-Trace-Id";
	public static final String MDC_TRACE_ID = "traceId";

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		// 1) 从请求头读取 traceId；若不存在则生成新的 traceId
		String incomingTraceId = exchange.getRequest().getHeaders().getFirst(HEADER_TRACE_ID);
		String traceId = StringUtils.hasText(incomingTraceId) ? incomingTraceId : generateTraceId();

		// 2) 写入 MDC，便于日志打印关联
		MDC.put(MDC_TRACE_ID, traceId);

		// 3) 将 traceId 透传到下游服务（写入请求头）
		ServerHttpRequest mutated = exchange.getRequest()
				.mutate()
				.header(HEADER_TRACE_ID, traceId)
				.build();

		return chain.filter(exchange.mutate().request(mutated).build())
				// 4) 清理 MDC，避免线程复用污染
				.doFinally(signalType -> MDC.remove(MDC_TRACE_ID));
	}

	private String generateTraceId() {
		return UUID.randomUUID().toString().replace("-", ""); // 简洁稳定的随机 ID
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE; // 最早执行，后续过滤器可直接使用 traceId
	}
}


