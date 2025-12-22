package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【角色分配权限 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "角色分配权限请求参数")
public class RoleAssignPermissionDto {

    @NotNull(message = "角色ID不能为空")
    @Schema(description = "角色ID", example = "1")
    private Long roleId;

    @NotEmpty(message = "权限ID列表不能为空")
    @Schema(description = "权限ID列表", example = "[12, 13, 14]")
    private List<Long> permissionIds;
}

