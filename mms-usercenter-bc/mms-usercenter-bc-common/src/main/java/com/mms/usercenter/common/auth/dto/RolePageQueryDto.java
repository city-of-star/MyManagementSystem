package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【角色分页查询请求 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "角色分页查询请求参数")
public class RolePageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "角色编码（模糊查询）", example = "admin")
    private String roleCode;

    @Schema(description = "角色名称（模糊查询）", example = "超级管理员")
    private String roleName;

    @Schema(description = "角色类型：system-系统角色，custom-自定义角色", example = "custom")
    private String roleType;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "创建时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}

