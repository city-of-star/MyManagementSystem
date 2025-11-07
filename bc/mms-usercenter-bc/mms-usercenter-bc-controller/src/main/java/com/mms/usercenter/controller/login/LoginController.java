package com.mms.usercenter.controller.login;

import com.mms.common.web.response.Response;
import com.mms.usercenter.common.login.dto.LoginDto;
import com.mms.usercenter.common.login.vo.LoginVo;
import com.mms.usercenter.service.login.service.LoginService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
@RestController
public class LoginController {

    @Resource
    private LoginService loginService;

    @PostMapping("/login")
    public Response<LoginVo> login(@RequestBody LoginDto dto) {
        return Response.success(loginService.login(dto));
    }
}