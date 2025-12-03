package com.mms.common.web.context;

import com.mms.common.core.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * 实现功能【用户上下文工具类】
 * <p>
 * 从请求头中提取用户信息（用户名、IP等），封装为 UserContext
 * 网关会将用户信息放入请求头，下游服务通过此工具类获取
 * 注意：
 * - 无参数方法会自动从当前请求线程获取请求对象，仅在 Spring MVC 请求线程中可用
 * - 本工具类位于 web-mvc 模块，仅适用于 Spring MVC 业务服务，不适用于网关（WebFlux）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-02 11:15:16
 */
public class UserContextUtils {

    /**
     * 用户名请求头（与网关保持一致）
     */
    private static final String HEADER_USER_NAME = "X-User-Name";

    /**
     * 客户端IP请求头（与网关保持一致）
     */
    private static final String HEADER_CLIENT_IP = "X-Client-Ip";

    /**
     * 获取当前请求的用户上下文（自动从请求线程获取）
     * <p>
     * 推荐使用此方法，无需传递 HttpServletRequest 参数
     * </p>
     *
     * @return 用户上下文，如果不在请求线程中或请求头中没有用户信息则返回null
     */
    public static UserContext getUserContext() {
        HttpServletRequest request = getCurrentRequest();
        if (request == null) {
            return null;
        }
        return getUserContext(request);
    }

    /**
     * 从指定请求中获取用户上下文
     * <p>
     * 用于特殊场景，如测试或需要从特定请求获取信息
     * </p>
     *
     * @param request HTTP请求对象
     * @return 用户上下文，如果请求头中没有用户信息则返回null
     */
    public static UserContext getUserContext(HttpServletRequest request) {
        if (request == null) {
            return null;
        }

        String username = request.getHeader(HEADER_USER_NAME);
        String clientIp = request.getHeader(HEADER_CLIENT_IP);

        // 如果用户名和IP都为空，返回null
        if (!StringUtils.hasText(username) && !StringUtils.hasText(clientIp)) {
            return null;
        }

        return new UserContext(username, clientIp);
    }

    /**
     * 获取当前登录用户名（自动从请求线程获取）
     *
     * @return 用户名，如果不存在则返回null
     */
    public static String getUsername() {
        UserContext context = getUserContext();
        return context != null ? context.getUsername() : null;
    }

    /**
     * 获取当前请求的客户端IP（自动从请求线程获取）
     *
     * @return 客户端IP，如果不存在则返回null
     */
    public static String getClientIp() {
        UserContext context = getUserContext();
        return context != null ? context.getClientIp() : null;
    }

    /**
     * 获取当前请求对象
     * <p>
     * 从 RequestContextHolder 中获取当前请求，仅在 Spring MVC 请求线程中可用
     * </p>
     *
     * @return 当前请求对象，如果不在请求线程中则返回null
     */
    private static HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest();
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    /**
     * 私有构造函数，防止实例化
     */
    private UserContextUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

