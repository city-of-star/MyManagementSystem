package com.mms.usercenter.common.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * 实现功能【刷新Token请求DTO】
 * <p>
 * 用于刷新Access Token的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:43:33
 */
@Data
public class RefreshTokenDto {

    @NotBlank(message = "刷新令牌不能为空")
    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;
}