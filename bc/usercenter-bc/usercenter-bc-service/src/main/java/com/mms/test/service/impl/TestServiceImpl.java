package com.mms.test.service.impl;

import com.mms.test.service.TestService;
import com.mms.test.feign.BaseTestFeign;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * 实现功能【测试服务实现类】
 *
 * @author li.hongyu
 * @date 2025-10-12 20:03:34
 */
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private BaseTestFeign baseTestFeign;

    @Override
    public String test() {
        String baseResult = baseTestFeign.test1();
        return "测试成功 -> base返回: " + baseResult;
    }
}