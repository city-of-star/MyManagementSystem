package com.mms.base.common.test.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【分页查询测试列表 DTO】
 *
 * @author li.hongyu
 * @date 2025-10-14 09:46:01
 */
@Data
public class TestDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 10;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

}