package com.mms.gateway.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【网关路由基础配置】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-10 15:36:17
 */
@Configuration
public class GatewayRouteConfiguration {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {

        return builder.routes()
                // 定义用户中心服务的路由规则
                .route("usercenter-service", predicateSpec -> predicateSpec
                        // 路径断言：匹配以/usercenter开头的所有请求
                        .path("/usercenter/**")
                        // 过滤器配置：去除路径前缀的第一部分（即去掉/usercenter）
                        .filters(filterSpec -> filterSpec.stripPrefix(1))
                        // 目标服务URI：使用负载均衡方式指向名为"usercenter"的服务
                        // lb:// 表示使用LoadBalancer进行负载均衡
                        .uri("lb://usercenter"))
                // 可以继续添加其他服务的路由规则
                // .route("other-service", ...)
                .build();

    }
}

