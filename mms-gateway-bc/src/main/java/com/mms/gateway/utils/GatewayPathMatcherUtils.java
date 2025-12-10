package com.mms.gateway.utils;

import org.springframework.http.server.PathContainer;
import org.springframework.web.util.pattern.PathPattern;
import org.springframework.web.util.pattern.PathPatternParser;

import java.util.List;

/**
 * 实现功能【网关路径匹配工具类】
 * <p>
 * 统一处理路径匹配逻辑，支持白名单匹配
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:38:28
 */
public class GatewayPathMatcherUtils {

    /**
     * 路径模式解析器（线程安全）
     */
    private static final PathPatternParser PATH_PATTERN_PARSER = new PathPatternParser();

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

        PathContainer pathContainer = PathContainer.parsePath(path);
        for (String pattern : whitelist) {
            PathPattern compiledPattern = PATH_PATTERN_PARSER.parse(pattern);
            if (compiledPattern.matches(pathContainer)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 检查路径是否匹配指定的模式
     *
     * @param path    待检查的路径
     * @param pattern 路径模式（支持 PathPattern 语法）
     * @return 如果匹配则返回 true，否则返回 false
     */
    public static boolean matches(String path, String pattern) {
        if (path == null || pattern == null) {
            return false;
        }

        PathContainer pathContainer = PathContainer.parsePath(path);
        PathPattern compiledPattern = PATH_PATTERN_PARSER.parse(pattern);
        return compiledPattern.matches(pathContainer);
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayPathMatcherUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}