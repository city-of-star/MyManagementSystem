package com.mms.common.web.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.util.UUID;

/**
 * 实现功能【Servlet/MVC TraceId 过滤器】
 * <p>
 * - 从请求头读取 X-Trace-Id（若无则生成）
 * - 放入 MDC("traceId")，以便 Response 序列化时写回 traceId
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE) // 最高优先级，确保最早执行
public class TraceIdMvcFilter implements Filter {

	// 透传用的请求头名
	public static final String HEADER_TRACE_ID = "X-Trace-Id";
	// 日志 MDC 键名
	public static final String MDC_TRACE_ID = "traceId";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request; // 只关心 HTTP 请求

		// 优先从上游（如网关）读取追踪 ID
		String traceId = httpRequest.getHeader(HEADER_TRACE_ID);
		// 若上游未提供，则本地生成
		if (!StringUtils.hasText(traceId)) {
			traceId = generateTraceId();
		}

		// 将追踪 ID 放进 MDC，便于日志打印和响应序列化写回
		MDC.put(MDC_TRACE_ID, traceId);

		try {
			// 放行给下游（Controller/拦截器等）
			chain.doFilter(request, response);
		} finally {
			// 清理 MDC，防止线程复用造成脏数据
			MDC.remove(MDC_TRACE_ID);
		}
	}

	/**
	 * 生成追踪 ID
	 */
	private String generateTraceId() {
		// 使用 UUID 并移除连字符，生成简洁且足够分布式的唯一 ID
		return UUID.randomUUID().toString().replace("-", "");
	}
}

