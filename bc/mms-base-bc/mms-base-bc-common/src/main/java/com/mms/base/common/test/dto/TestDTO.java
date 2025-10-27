package com.mms.base.common.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【分页查询测试列表 DTO】
 *
 * @author li.hongyu
 * @date 2025-10-14 09:46:01
 */
@Data
@Schema(description = "测试查询条件")
public class TestDTO {

    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10", defaultValue = "10")
    private Integer pageSize = 10;

    @Schema(description = "标题关键字", example = "测试标题")
    private String title;

    @Schema(description = "开始时间", example = "2025-01-01T00:00:00")
    private LocalDateTime startTime;

    @Schema(description = "结束时间", example = "2025-12-31T23:59:59")
    private LocalDateTime endTime;

}