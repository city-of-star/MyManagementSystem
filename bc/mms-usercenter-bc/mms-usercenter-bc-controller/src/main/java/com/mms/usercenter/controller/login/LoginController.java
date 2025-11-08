package com.mms.usercenter.controller.login;

import com.mms.common.web.response.Response;
import com.mms.usercenter.common.login.dto.LoginDto;
import com.mms.usercenter.common.login.vo.LoginVo;
import com.mms.usercenter.service.login.service.LoginService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@Tag(name = "用户登录", description = "用户登录相关接口")
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Resource
    private LoginService loginService;

    @Operation(summary = "用户登录", description = "通过用户名和密码进行登录，返回JWT Token")
    @PostMapping("/login")
    public Response<LoginVo> login(@RequestBody @Valid LoginDto dto) {
        return Response.success(loginService.login(dto));
    }
}