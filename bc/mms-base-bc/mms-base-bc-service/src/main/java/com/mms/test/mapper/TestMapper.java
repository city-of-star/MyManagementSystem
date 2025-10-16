package com.mms.test.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.test.dto.TestDTO;
import com.mms.test.entity.TestEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【测试 Mapper】
 *
 * @author li.hongyu
 * @date 2025-10-14 09:33:53
 */
@Mapper
public interface TestMapper extends BaseMapper<TestEntity> {

    /**
     * 分页查询测试列表
     * @param page 分页条件
     * @param dto 查询条件
     * @return 分页测试列表
     */
    Page<TestEntity> getPage(@Param("page") Page<TestEntity> page, @Param("dto") TestDTO dto);
}