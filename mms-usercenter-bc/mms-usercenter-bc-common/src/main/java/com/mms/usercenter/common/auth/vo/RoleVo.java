package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【角色响应 VO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "角色信息响应对象")
public class RoleVo {

    @Schema(description = "角色ID", example = "1")
    private Long id;

    @Schema(description = "角色编码", example = "admin")
    private String roleCode;

    @Schema(description = "角色名称", example = "超级管理员")
    private String roleName;

    @Schema(description = "角色类型：system-系统角色，custom-自定义角色", example = "custom")
    private String roleType;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "系统内置角色")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-12-22 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-12-22 10:00:00")
    private LocalDateTime updateTime;
}

