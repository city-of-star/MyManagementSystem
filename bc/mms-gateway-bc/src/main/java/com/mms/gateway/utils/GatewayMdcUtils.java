package com.mms.gateway.utils;

import com.mms.gateway.constants.GatewayConstants;
import org.slf4j.MDC;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * 实现功能【网关 MDC 工具类】
 * <p>
 * 统一处理 MDC（Mapped Diagnostic Context）操作，便于日志追踪
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-12
 */
public class GatewayMdcUtils {

    /**
     * 从请求头中提取 TraceId 并放入 MDC
     *
     * @param exchange 请求交换对象
     */
    public static void putTraceIdFromRequest(ServerWebExchange exchange) {
        String traceId = exchange.getRequest().getHeaders().getFirst(GatewayConstants.Headers.TRACE_ID);
        if (StringUtils.hasText(traceId)) {
            MDC.put(GatewayConstants.Mdc.TRACE_ID, traceId);
        }
    }

    /**
     * 将 TraceId 放入 MDC
     *
     * @param traceId 追踪 ID
     */
    public static void putTraceId(String traceId) {
        if (StringUtils.hasText(traceId)) {
            MDC.put(GatewayConstants.Mdc.TRACE_ID, traceId);
        }
    }

    /**
     * 从请求头中获取 TraceId
     *
     * @param exchange 请求交换对象
     * @return TraceId，如果不存在则返回 null
     */
    public static String getTraceIdFromRequest(ServerWebExchange exchange) {
        return exchange.getRequest().getHeaders().getFirst(GatewayConstants.Headers.TRACE_ID);
    }

    /**
     * 从 MDC 中获取 TraceId
     *
     * @return TraceId，如果不存在则返回 null
     */
    public static String getTraceIdFromMdc() {
        return MDC.get(GatewayConstants.Mdc.TRACE_ID);
    }

    /**
     * 移除 MDC 中的 TraceId
     */
    public static void removeTraceId() {
        MDC.remove(GatewayConstants.Mdc.TRACE_ID);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayMdcUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

