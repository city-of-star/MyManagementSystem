package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【权限分页查询 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 12:00:00
 */
@Data
@Schema(description = "权限分页查询请求参数")
public class PermissionPageQueryDto {

    @Schema(description = "页码，从1开始", example = "1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小", example = "10")
    private Integer pageSize = 10;

    @Schema(description = "父权限ID，0表示顶级", example = "0")
    private Long parentId;

    @Schema(description = "权限类型：menu-菜单，button-按钮，api-接口", example = "menu")
    private String permissionType;

    @Schema(description = "权限名称（模糊查询）", example = "用户")
    private String permissionName;

    @Schema(description = "权限编码（模糊查询）", example = "user")
    private String permissionCode;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "显示状态：0-隐藏，1-显示", example = "1")
    private Integer visible;

    @Schema(description = "创建时间开始", example = "2025-01-01 00:00:00")
    private LocalDateTime createTimeStart;

    @Schema(description = "创建时间结束", example = "2025-12-31 23:59:59")
    private LocalDateTime createTimeEnd;
}

