package com.mms.test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.test.dto.TestDTO;
import com.mms.test.entity.TestEntity;
import com.mms.test.service.TestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

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

    @PostMapping("/getPage")
    public Page<TestEntity> getPage(@RequestBody TestDTO dto) {
        return testService.getPage(dto);
    }
}