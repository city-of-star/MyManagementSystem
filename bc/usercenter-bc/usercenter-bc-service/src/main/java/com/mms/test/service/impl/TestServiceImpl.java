package com.mms.test.service.impl;

import com.mms.test.service.TestService;
import org.springframework.stereotype.Service;

/**
 * 实现功能【测试服务实现类】
 *
 * @author li.hongyu
 * @date 2025-10-12 20:03:34
 */
@Service
public class TestServiceImpl implements TestService {

    @Override
    public String test() {
        return "测试成功";
    }
}