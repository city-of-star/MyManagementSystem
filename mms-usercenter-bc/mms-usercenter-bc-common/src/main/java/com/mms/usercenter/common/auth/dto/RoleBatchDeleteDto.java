package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【角色批量删除 DTO】
 *
 * @author li.hongyu
 * @date 2025-12-22 11:00:00
 */
@Data
@Schema(description = "角色批量删除请求参数")
public class RoleBatchDeleteDto {

    @NotEmpty(message = "角色ID列表不能为空")
    @Schema(description = "角色ID列表", example = "[1,2,3]")
    private List<Long> roleIds;
}

