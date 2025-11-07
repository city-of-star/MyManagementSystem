package com.mms.usercenter.service.login.service;

import com.mms.usercenter.common.login.dto.LoginDto;
import com.mms.usercenter.common.login.vo.LoginVo;

/**
 * 实现功能【用户登录注册服务】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 11:39:24
 */
public interface LoginService {

    /**
     * 用户登录
     * @param dto 用户名、密码
     * @return 访问令牌
     */
    LoginVo login(LoginDto dto);
}