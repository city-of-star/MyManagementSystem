package com.mms.usercenter.service.login.service.impl;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.common.utils.JwtUtil;
import com.mms.usercenter.common.login.dto.LoginDto;
import com.mms.usercenter.common.login.entity.SysUserEntity;
import com.mms.usercenter.common.login.vo.LoginVo;
import com.mms.usercenter.service.common.mapper.SysUserMapper;
import com.mms.usercenter.service.login.service.LoginService;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 实现功能【用户登录注册服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 11:39:50
 */
@Service
public class LoginServiceImpl implements LoginService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Override
    public LoginVo login(LoginDto dto) {
        // 查询用户
        SysUserEntity user = sysUserMapper.selectByUsername(dto.getUsername());

        // 验证用户是否存在
        if (user == null) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 验证账号状态
        if (user.getStatus() == 0) {
            throw new BusinessException(ErrorCode.ACCOUNT_DISABLED);
        }

        // 验证账号是否锁定
        if (user.getLocked() == 1) {
            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED);
        }

        // 验证密码
        if (!BCrypt.checkpw(dto.getPassword(), user.getPassword())) {
            throw new BusinessException(ErrorCode.LOGIN_FAILED);
        }

        // 更新最后登录时间和IP（可选，需要获取客户端IP）
        // user.setLastLoginTime(LocalDateTime.now());
        // user.setLastLoginIp(getClientIp());
        // sysUserMapper.updateById(user);

        // 生成 JWT Token
        String token = jwtUtil.generateToken(user.getId());

        // 返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setToken(token);
        return loginVo;
    }
}