package com.mms.base.service.test.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.test.dto.TestDTO;
import com.mms.base.common.test.entity.TestEntity;
import com.mms.base.service.test.mapper.TestMapper;
import com.mms.base.service.test.service.TestService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 实现功能【测试服务实现类】
 *
 * @author li.hongyu
 * @date 2025-10-12 20:03:34
 */
@Slf4j
@Service
public class TestServiceImpl implements TestService {

    @Resource
    private TestMapper testMapper;

    @Override
    public String test() {

        return "测试成功";
    }

    @Override
    public Page<TestEntity> getPage(TestDTO dto) {
        try {
            log.info("---------------------------分页查询测试列表入参：{} ---------------------------", dto.toString());
            Page<TestEntity> page = new Page<>(dto.getPageNum(), dto.getPageSize());
            Page<TestEntity> res = testMapper.getPage(page, dto);
            return res;
        } catch (Exception e) {
            log.error("---------------------------分页查询测试列表失败：{} ---------------------------", e.getMessage());
            return null;
        }
    }
}