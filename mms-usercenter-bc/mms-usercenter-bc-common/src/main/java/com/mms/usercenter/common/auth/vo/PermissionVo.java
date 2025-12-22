package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 实现功能【权限响应 VO】
 *
 * @author li.hongyu
 * @date 2025-12-22 12:00:00
 */
@Data
@Schema(description = "权限信息响应对象")
public class PermissionVo {

    @Schema(description = "权限ID", example = "1")
    private Long id;

    @Schema(description = "父权限ID，0表示顶级权限", example = "0")
    private Long parentId;

    @Schema(description = "权限类型：menu-菜单，button-按钮，api-接口", example = "menu")
    private String permissionType;

    @Schema(description = "权限名称", example = "用户管理")
    private String permissionName;

    @Schema(description = "权限编码（唯一标识）", example = "user:manage")
    private String permissionCode;

    @Schema(description = "路由路径（菜单类型）", example = "/user")
    private String path;

    @Schema(description = "组件路径（菜单类型）", example = "user/index")
    private String component;

    @Schema(description = "图标（菜单类型）", example = "user")
    private String icon;

    @Schema(description = "接口URL（接口类型）", example = "/api/user/list")
    private String apiUrl;

    @Schema(description = "接口请求方式：GET,POST,PUT,DELETE", example = "GET")
    private String apiMethod;

    @Schema(description = "排序号", example = "1")
    private Integer sortOrder;

    @Schema(description = "是否显示：0-隐藏，1-显示", example = "1")
    private Integer visible;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "备注", example = "权限说明")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-12-22 10:00:00")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-12-22 10:00:00")
    private LocalDateTime updateTime;

    @Schema(description = "子权限列表")
    private List<PermissionVo> children = new ArrayList<>();
}

