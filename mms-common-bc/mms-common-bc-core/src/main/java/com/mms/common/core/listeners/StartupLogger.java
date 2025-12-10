package com.mms.common.core.listeners;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 实现功能【应用启动成功日志记录】
 * <p>
 * 应用启动完成后打印应用基本信息与相应的 Knife4j 文档地址
 * 网关本身不暴露 Knife4j，自动跳过
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-10 16:12:56
 */
@Component
public class StartupLogger implements ApplicationListener<ApplicationReadyEvent> {

    @Value("${spring.application.name:application}")
    private String applicationName;

    @Value("${spring.profiles.active:default}")
    private String activeProfile;

    @Value("${server.port:8080}")
    private int port;

    @Value("${info.app.version:unknown}")
    private String appVersion;

    @Value("${info.app.build-time:unknown}")
    private String buildTime;

    @Autowired(required = false)
    private BuildProperties buildProperties;

    @Value("${server.servlet.context-path:}")
    private String contextPath;

    @Value("${spring.cloud.nacos.server-addr:unknown}")
    private String nacosServerAddr;

    @Value("${spring.cloud.nacos.config.namespace:${spring.cloud.nacos.discovery.namespace:unknown}}")
    private String nacosNamespace;

    @Value("${spring.cloud.nacos.config.group:${spring.cloud.nacos.discovery.group:DEFAULT_GROUP}}")
    private String nacosGroup;

    @Value("${logback.path:logs}")
    private String logPath;

    /**
     * 为不同环境预先配置域名。
     */
    @Value("${swagger.base-url:}")
    private String swaggerBaseUrl;

    /**
     * Knife4j UI 入口路径，默认 /doc.html。
     */
    @Value("${swagger.ui-path:/doc.html}")
    private String swaggerUiPath;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        // 网关不暴露 Knife4j，直接跳过
        if ("gateway".equalsIgnoreCase(applicationName)) {
            return;
        }

        // 在应用完全就绪时输出自定义 banner（位于各服务资源目录的 banner.txt）
        printBannerIfPresent();

        // 拼接baseUrl
        String base;
        if (swaggerBaseUrl == null || swaggerBaseUrl.isBlank()) {
            String ctx = normalizeContextPath(contextPath);
            base = "http://localhost:" + port + ctx;
        } else {
            base = stripTrailingSlash(swaggerBaseUrl);
        }

        // swagger文档的地址
        String swaggerUrl = base + ensureLeadingSlash(swaggerUiPath);

        // 打印启动完成与文档地址及基础环境信息
        System.out.println("\n==== 应用启动信息 ====");
        System.out.println("应用名: " + applicationName);
        System.out.println("环境: " + activeProfile);
        System.out.println("端口: " + port);
        System.out.println("版本: " + resolveVersion());
        System.out.println("构建时间: " + resolveBuildTime());
        System.out.println("日志目录: " + logPath);
        System.out.println("Nacos: " + nacosServerAddr + " | namespace=" + nacosNamespace + " | group=" + nacosGroup);
        System.out.println("Swagger 文档地址: " + swaggerUrl);
        System.out.println("==== 应用就绪 ====\n");
    }

    /**
     * 在就绪后输出 classpath 下的 banner.txt，确保最后呈现。
     */
    private void printBannerIfPresent() {
        ClassPathResource resource = new ClassPathResource("banner.txt");
        if (!resource.exists()) {
            return;
        }
        try (InputStream is = resource.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // 出现 IO 问题时仅记录，不影响启动
            System.out.println("读取 banner.txt 失败：" + e.getMessage());
        }
    }

    private String stripTrailingSlash(String url) {
        return url != null && url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
    }

    private String normalizeContextPath(String ctx) {
        if (ctx == null || ctx.isBlank()) {
            return "";
        }
        String withLeading = ensureLeadingSlash(ctx);
        return stripTrailingSlash(withLeading);
    }

    private String ensureLeadingSlash(String path) {
        return path != null && path.startsWith("/") ? path : "/" + path;
    }

    private String resolveVersion() {
        if (buildProperties != null && buildProperties.getVersion() != null && !buildProperties.getVersion().isBlank()) {
            return buildProperties.getVersion();
        }
        return appVersion;
    }

    private String resolveBuildTime() {
        String timeStr = null;

        if (buildProperties != null && buildProperties.getTime() != null) {
            timeStr = buildProperties.getTime().toString();
        }

        if (timeStr != null) {
            try {
                // 解析 ISO 8601 格式的时间（如 2025-12-10T11:28:13.377Z）
                Instant instant = Instant.parse(timeStr);

                // 转换为本地时区（默认使用系统时区，这里是北京时间 CST = UTC+8）
                ZoneId zoneId = ZoneId.systemDefault();
                ZonedDateTime localTime = instant.atZone(zoneId);

                // 格式化为可读性更强的时间格式
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                return formatter.format(localTime);

            } catch (Exception e) {
                // 如果解析失败，返回原始字符串
                return timeStr;
            }
        }

        return "unknown";
    }
}

