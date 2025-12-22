package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【权限批量删除 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 12:00:00
 */
@Data
@Schema(description = "权限批量删除请求参数")
public class PermissionBatchDeleteDto {

    @NotEmpty(message = "权限ID列表不能为空")
    @Schema(description = "权限ID列表", example = "[1,2,3]")
    private List<Long> permissionIds;
}

