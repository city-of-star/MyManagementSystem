package com.mms.gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
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
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceFilter implements GlobalFilter {

	// 追踪 ID 的请求头名称
	public static final String HEADER_TRACE_ID = "X-Trace-Id";
	// MDC 中存储追踪 ID 的键名
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
				.header(HEADER_TRACE_ID, traceId) // 添加追踪 ID 到请求头
				.build();

		// 4) 继续过滤器链，并在完成后清理 MDC
		return chain.filter(exchange.mutate().request(mutated).build())
				.doFinally(signalType -> MDC.remove(MDC_TRACE_ID)); // 清理 MDC，避免线程复用污染
	}

	/**
	 * 生成追踪 ID
	 */
	private String generateTraceId() {
		// 生成简洁稳定的随机 ID
		return UUID.randomUUID().toString().replace("-", "");
	}
}


