package com.mms.usercenter.controller.auth;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.response.Response;
import com.mms.usercenter.common.auth.dto.LoginDto;
import com.mms.usercenter.common.auth.dto.LogoutDto;
import com.mms.usercenter.common.auth.dto.RefreshTokenDto;
import com.mms.usercenter.common.auth.vo.LoginVo;
import com.mms.usercenter.service.auth.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【用户登录注册服务 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 14:20:55
 */
@Tag(name = "用户认证", description = "用户认证相关接口")
@RestController
@RequestMapping("/auth")
public class AuthController {

    @Resource
    private AuthService authService;

    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回【Access Token】和【Refresh Token】")
    @PostMapping("/login")
    public Response<LoginVo> login(@RequestBody @Valid LoginDto dto) {
        return Response.success(authService.login(dto));
    }

    @Operation(summary = "刷新Token", description = "使用【Refresh Token】刷新【Access Token】和【Refresh Token】")
    @PostMapping("/refresh")
    public Response<LoginVo> refreshToken(@RequestBody @Valid RefreshTokenDto dto) {
        return Response.success(authService.refreshToken(dto));
    }

    @Operation(summary = "用户登出", description = "登出并让【Access Token】和【Refresh Token 失效】")
    @PostMapping("/logout")
    public Response<Void> logout(
            @RequestHeader(value = GatewayConstants.Headers.TOKEN_JTI, required = false) String accessTokenJti,
            @RequestHeader(value = GatewayConstants.Headers.TOKEN_EXP, required = false) String accessTokenExp,
            @RequestBody @Valid LogoutDto dto) {
        // 从网关透传的请求头获取Access Token信息（网关已验证）
        authService.logout(accessTokenJti, accessTokenExp, dto);
        return Response.success();
    }
}