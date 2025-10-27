package com.mms.usercenter.controller.test;

import com.mms.usercenter.service.test.service.TestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
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

    @GetMapping("/1")
    public String test1() {
        return testService.test();
    }

}