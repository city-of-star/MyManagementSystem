package com.mms.test;

import com.mms.test.service.TestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【测试基础功能】
 *
 * @author li.hongyu
 * @date 2025-10-12 19:33:28
 */
@Slf4j
@RestController
@RequestMapping("/test")
@RefreshScope
public class TestController {

    @Resource
    private TestService testService;

    // 测试从Nacos读取配置
    @Value("${test.message:未读取到配置}")
    private String nacosConfig;
    
    @Value("${test.value:0}")
    private Integer nacosValue;
    
    @Value("${spring.datasource.url:未读取到数据库配置}")
    private String datasourceUrl;

    @GetMapping("/1")
    public String test1() {
        return testService.test();
    }

    @GetMapping("/nacos")
    public String testNacosConfig() {
        log.info("从Nacos读取的配置: {}", nacosConfig);
        return "从Nacos读取的配置: " + nacosConfig;
    }
    
    @GetMapping("/nacos-detail")
    public String testNacosConfigDetail() {
        StringBuilder result = new StringBuilder();
        result.append("Nacos配置验证结果:\n");
        result.append("test.message: ").append(nacosConfig).append("\n");
        result.append("test.value: ").append(nacosValue).append("\n");
        result.append("数据库URL: ").append(datasourceUrl).append("\n");
        
        log.info("Nacos配置详情: {}", result.toString());
        return result.toString();
    }
}