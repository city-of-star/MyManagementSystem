package com.mms.gateway.config;

import com.mms.gateway.utils.GatewayPathMatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【网关白名单配置类】
 * <p>
 * 统一管理网关白名单路径，支持通过配置文件动态配置
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:36:17
 */
@Configuration
@ConfigurationProperties(prefix = "gateway.whitelist")
public class GatewayWhitelistConfig {

    /**
     * 白名单路径列表
     */
    private List<String> paths = new ArrayList<>();

    /**
     * 默认白名单路径（如果配置文件中没有配置，则使用默认值）
     */
    public GatewayWhitelistConfig() {
        // 初始化默认白名单
        this.paths.add("/actuator/**");                // Spring Boot Actuator 端点
        this.paths.add("/usercenter/auth/login");      // 登录
        this.paths.add("/usercenter/auth/refresh");    // 刷新
        this.paths.add("/doc.html");                   // Knife4j 主页面
        this.paths.add("/v3/api-docs/**");             // OpenAPI 文档
        this.paths.add("/webjars/**");                 // Knife4j 静态资源
        this.paths.add("/swagger-resources/**");       // Swagger 资源
    }

    /**
     * 获取白名单路径列表
     *
     * @return 白名单路径列表
     */
    public List<String> getPaths() {
        return paths;
    }

    /**
     * 设置白名单路径列表
     *
     * @param paths 白名单路径列表
     */
    public void setPaths(List<String> paths) {
        this.paths = paths != null ? paths : new ArrayList<>();
    }

    /**
     * 检查路径是否在白名单中
     *
     * @param path 待检查的路径
     * @return 如果路径在白名单中则返回 true
     */
    public boolean isWhitelisted(String path) {
        return GatewayPathMatcher.isWhitelisted(path, this.paths);
    }
}

