package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实现功能【登出请求DTO】
 * <p>
 * 用于用户登出的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:43:41
 */
@Data
@Schema(description = "登出请求参数")
public class LogoutDto {

    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "刷新令牌不能为空")
    private String refreshToken;
}