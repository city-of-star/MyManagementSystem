package com.mms.usercenter.service.auth.service.impl;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.usercenter.common.common.utils.JwtUtil;
import com.mms.usercenter.common.auth.dto.LoginDto;
import com.mms.usercenter.common.auth.entity.SysUserEntity;
import com.mms.usercenter.common.auth.vo.LoginVo;
import com.mms.usercenter.service.auth.config.LoginSecurityConfig;
import com.mms.usercenter.service.auth.service.LoginSecurityService;
import com.mms.usercenter.service.common.mapper.SysUserMapper;
import com.mms.usercenter.service.auth.service.AuthService;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

/**
 * 实现功能【用户认证服务实现类】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 11:39:50
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Resource
    private SysUserMapper sysUserMapper;

    @Resource
    private JwtUtil jwtUtil;

    @Resource
    private LoginSecurityService loginSecurityService;

    @Resource
    private LoginSecurityConfig securityConfig;

    @Override
    public LoginVo login(LoginDto dto) {
        try {
            if (loginSecurityService.isAccountLocked(dto.getUsername())) {
                long remainingTime = loginSecurityService.getLockRemainingTime(dto.getUsername());
                throw new BusinessException(ErrorCode.ACCOUNT_LOCKED,
                        String.format("账号已被锁定，请在 %d 分钟后重试", remainingTime / 60));
            }

            // 查询用户
            SysUserEntity user = sysUserMapper.selectByUsername(dto.getUsername());

            // 验证用户是否存在
            if (user == null) {
                handleLoginFailure(dto.getUsername(), null);
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
                handleLoginFailure(dto.getUsername(), user);
                throw new BusinessException(ErrorCode.LOGIN_FAILED);
            }

            // 登录成功，重置失败次数
            loginSecurityService.resetLoginAttempts(dto.getUsername());

            // 更新最后登录时间和IP（需要获取客户端IP）
            // user.setLastLoginTime(LocalDateTime.now());
            // user.setLastLoginIp(getClientIp());
            // sysUserMapper.updateById(user);

            // 生成 JWT Token
            String token = jwtUtil.generateToken(dto.getUsername());

            // 返回结果
            LoginVo loginVo = new LoginVo();
            loginVo.setToken(token);
            return loginVo;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException();
        }
    }

    /**
     * 处理登录失败逻辑
     */
    private void handleLoginFailure(String username, SysUserEntity user) {
        // 增加失败次数
        loginSecurityService.incrementLoginAttempts(username);

        // 获取失败次数
        int attempts = loginSecurityService.getLoginAttempts(username);

        // 如果达到最大尝试次数，锁定账号
        if (attempts >= securityConfig.getMaxAttempts()) {

            // 锁定账号
            loginSecurityService.lockAccount(username);

            // 更新数据库中的锁定状态
             if (user != null) {
                 user.setLocked(1);
                 sysUserMapper.updateById(user);
             }

            throw new BusinessException(ErrorCode.ACCOUNT_LOCKED,
                    String.format("登录失败次数过多，账号已被锁定 %d 分钟", securityConfig.getLockTime()));
        }

        // 获取剩余尝试次数
        int remainingAttempts = securityConfig.getMaxAttempts() - attempts;

        // 提示剩余尝试次数
        throw new BusinessException(ErrorCode.LOGIN_FAILED,
                String.format("用户名或密码错误，您还有 %d 次尝试机会", remainingAttempts));
    }

    /**
     * 解锁账号（管理员使用）
     */
    public void unlockAccount(String username) {

        // 重置登录失败次数
        loginSecurityService.resetLoginAttempts(username);

        // 删除锁定状态
        loginSecurityService.clearAccountLock(username);

        // 更新数据库中的锁定状态
         SysUserEntity user = sysUserMapper.selectByUsername(username);
         if (user != null) {
             user.setLocked(0);
             sysUserMapper.updateById(user);
         }
    }

}