package com.mms.gateway.utils;

import org.springframework.util.AntPathMatcher;

import java.util.List;

/**
 * 实现功能【网关路径匹配工具类】
 * <p>
 * 统一处理路径匹配逻辑，支持白名单匹配
 * </p>
 *
 * @author li.hongyu
 * @date 2025-11-12
 */
public class GatewayPathMatcher {

    /**
     * 路径匹配器（线程安全）
     */
    private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    /**
     * 检查路径是否匹配白名单中的任意一个模式
     *
     * @param path      待检查的路径
     * @param whitelist 白名单路径模式列表
     * @return 如果匹配则返回 true，否则返回 false
     */
    public static boolean isWhitelisted(String path, List<String> whitelist) {
        if (path == null || whitelist == null || whitelist.isEmpty()) {
            return false;
        }
        for (String pattern : whitelist) {
            if (PATH_MATCHER.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查路径是否匹配指定的模式
     *
     * @param path    待检查的路径
     * @param pattern 路径模式（支持 Ant 风格）
     * @return 如果匹配则返回 true，否则返回 false
     */
    public static boolean matches(String path, String pattern) {
        if (path == null || pattern == null) {
            return false;
        }
        return PATH_MATCHER.match(pattern, path);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayPathMatcher() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

