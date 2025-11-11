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
@Order(Ordered.HIGHEST_PRECEDENCE) // 尽可能最早执行，确保后续链路都能拿到 traceId
public class TraceIdMvcFilter implements Filter {

	public static final String HEADER_TRACE_ID = "X-Trace-Id"; // 透传用的请求头名
	public static final String MDC_TRACE_ID = "traceId"; // 日志 MDC 键名

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request; // 只关心 HTTP 请求
		String traceId = httpRequest.getHeader(HEADER_TRACE_ID); // 优先从上游（如网关）读取
		if (!StringUtils.hasText(traceId)) {
			traceId = generateTraceId(); // 若上游未提供，则本地生成
		}
		MDC.put(MDC_TRACE_ID, traceId); // 放进 MDC，便于日志打印和响应序列化写回
		try {
			chain.doFilter(request, response); // 放行给下游（Controller/拦截器等）
		} finally {
			MDC.remove(MDC_TRACE_ID); // 防止线程复用造成脏数据
		}
	}

	private String generateTraceId() {
		return UUID.randomUUID().toString().replace("-", ""); // 简单且足够分布式唯一
	}
}


