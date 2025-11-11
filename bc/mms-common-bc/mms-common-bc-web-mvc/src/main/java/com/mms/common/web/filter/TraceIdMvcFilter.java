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
 * - 从请求头读取 X-Trace-Id（若无则生成）
 * - 放入 MDC("traceId")，以便 Response 序列化时写回 traceId
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TraceIdMvcFilter implements Filter {

	public static final String HEADER_TRACE_ID = "X-Trace-Id";
	public static final String MDC_TRACE_ID = "traceId";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		String traceId = httpRequest.getHeader(HEADER_TRACE_ID);
		if (!StringUtils.hasText(traceId)) {
			traceId = generateTraceId();
		}
		MDC.put(MDC_TRACE_ID, traceId);
		try {
			chain.doFilter(request, response);
		} finally {
			MDC.remove(MDC_TRACE_ID);
		}
	}

	private String generateTraceId() {
		return UUID.randomUUID().toString().replace("-", "");
	}
}


