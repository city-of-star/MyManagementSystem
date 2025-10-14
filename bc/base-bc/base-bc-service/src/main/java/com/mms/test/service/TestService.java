package com.mms.test.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.test.dto.TestDTO;
import com.mms.test.entity.TestEntity;

/**
 * 实现功能【测试服务】
 *
 * @author li.hongyu
 * @date 2025-10-12 20:02:55
 */
public interface TestService {

    String test();

    Page<TestEntity> getPage(TestDTO dto);
}