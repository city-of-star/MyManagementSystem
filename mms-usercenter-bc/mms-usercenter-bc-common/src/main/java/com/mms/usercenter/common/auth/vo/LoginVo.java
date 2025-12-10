package com.mms.usercenter.common.auth.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【用户登录返回 VO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 14:16:56
 */
@Data
public class LoginVo {

    @Schema(description = "访问令牌", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String accessToken;

    @Schema(description = "刷新令牌", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String refreshToken;

    @Schema(description = "访问令牌过期时间（秒）", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "900")
    private Long accessTokenExpiresIn;

    @Schema(description = "刷新令牌过期时间（秒）", requiredMode = Schema.RequiredMode.REQUIRED,
            example = "604800")
    private Long refreshTokenExpiresIn;
}