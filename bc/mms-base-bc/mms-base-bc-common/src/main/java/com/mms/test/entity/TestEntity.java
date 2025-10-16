package com.mms.test.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
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
public class TestEntity {

    @TableId(type = IdType.AUTO)
    private Integer id;

    private String title;

    private String content;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}