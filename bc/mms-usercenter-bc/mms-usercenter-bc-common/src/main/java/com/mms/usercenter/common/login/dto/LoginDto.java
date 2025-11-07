package com.mms.usercenter.common.login.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 实现功能【用户登录请求 DTO】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 14:11:09
 */
@Data
public class LoginDto {

    @Schema(description = "用户名", requiredMode = Schema.RequiredMode.REQUIRED, example = "admin")
    private String username;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED, example = "123456")
    private String password;

}