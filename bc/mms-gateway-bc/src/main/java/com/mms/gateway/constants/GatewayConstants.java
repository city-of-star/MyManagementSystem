package com.mms.gateway.constants;

import org.springframework.http.HttpHeaders;

/**
 * 实现功能【网关常量类】
 * <p>
 * 统一管理网关服务中使用的所有常量，避免重复定义
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-12
 */
public class GatewayConstants {

    /**
     * 请求头常量
     */
    public static class Headers {
        /**
         * Authorization 请求头
         */
        public static final String AUTHORIZATION = HttpHeaders.AUTHORIZATION;

        /**
         * TraceId 请求头
         */
        public static final String TRACE_ID = "X-Trace-Id";

        /**
         * 用户名请求头（透传到下游服务）
         */
        public static final String USER_NAME = "X-User-Name";

        /**
         * Bearer Token 前缀
         */
        public static final String BEARER_PREFIX = "Bearer ";
    }

    /**
     * MDC 常量
     */
    public static class Mdc {
        /**
         * TraceId 在 MDC 中的键名
         */
        public static final String TRACE_ID = "traceId";
    }

    /**
     * 过滤器顺序常量
     */
    public static class FilterOrder {
        /**
         * TraceFilter 的执行顺序（最高优先级）
         */
        public static final int TRACE_FILTER = Integer.MIN_VALUE;

        /**
         * JwtAuthFilter 的执行顺序（在 TraceFilter 之后）
         */
        public static final int JWT_AUTH_FILTER = TRACE_FILTER + 20;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}

