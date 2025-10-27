package com.mms.base.common.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

/**
 * 实现功能【测试实体类】
 *
 * @author li.hongyu
 * @date 2025-10-12 19:46:38
 */

@Data
@Accessors(chain = true)
@TableName("test")
@Schema(description = "测试实体")
public class TestEntity {

    @TableId(type = IdType.AUTO)
    @Schema(description = "主键ID", example = "1")
    private Integer id;

    @Schema(description = "标题", example = "测试标题")
    private String title;

    @Schema(description = "内容", example = "测试内容")
    private String content;

    @Schema(description = "创建时间", example = "2025-01-01T00:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新时间", example = "2025-01-01T00:00:00")
    private LocalDateTime updateTime;
}