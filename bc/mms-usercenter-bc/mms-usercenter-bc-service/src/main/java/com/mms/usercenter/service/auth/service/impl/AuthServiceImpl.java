package com.mms.usercenter.service.auth.service.impl;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.security.jwt.JwtConstants;
import com.mms.common.security.jwt.JwtUtil;
import com.mms.common.security.jwt.TokenType;
import com.mms.common.security.jwt.TokenValidator;
import com.mms.common.web.context.UserContextUtils;
import com.mms.usercenter.common.auth.dto.LoginDto;
import com.mms.usercenter.common.auth.dto.LogoutDto;
import com.mms.usercenter.common.auth.dto.RefreshTokenDto;
import com.mms.usercenter.common.auth.entity.SysUserEntity;
import com.mms.usercenter.common.auth.vo.LoginVo;
import com.mms.usercenter.service.auth.config.LoginSecurityConfig;
import com.mms.usercenter.service.auth.utils.LoginSecurityUtils;
import com.mms.usercenter.service.common.mapper.SysUserMapper;
import com.mms.usercenter.service.auth.service.AuthService;
import io.jsonwebtoken.Claims;
import jakarta.annotation.Resource;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Optional;

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
    private TokenValidator tokenValidator;

    @Resource
    private LoginSecurityUtils loginSecurityUtils;

    @Resource
    private LoginSecurityConfig securityConfig;

    @Override
    public LoginVo login(LoginDto dto) {
        try {
            if (loginSecurityUtils.isAccountLocked(dto.getUsername())) {
                long remainingTime = loginSecurityUtils.getLockRemainingTime(dto.getUsername());
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
            loginSecurityUtils.resetLoginAttempts(dto.getUsername());

            // 更新最后登录时间和IP
            user.setLastLoginTime(LocalDateTime.now());
            String clientIp = UserContextUtils.getClientIp();
            user.setLastLoginIp(StringUtils.hasText(clientIp) ? clientIp : "unknown");
            sysUserMapper.updateById(user);

            // 生成双 Token
            String accessToken = jwtUtil.generateAccessToken(dto.getUsername());
            String refreshToken = jwtUtil.generateRefreshToken(dto.getUsername());

            // 将Refresh Token存储到Redis（实现单点登录控制）
            tokenValidator.storeRefreshToken(dto.getUsername(), refreshToken);

            // 构建 LoginVo
            return buildLoginVo(accessToken, refreshToken);
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new ServerException();
        }
    }

    @Override
    public LoginVo refreshToken(RefreshTokenDto dto) {
        // 解析并验证Refresh Token
        Claims refreshClaims = tokenValidator.parseAndValidate(dto.getRefreshToken(), TokenType.REFRESH);

        // 提取用户名
        String username = Optional.ofNullable(refreshClaims.get(JwtConstants.Claims.USERNAME))
                .map(Object::toString)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "Token中缺少用户名信息"));

        // 验证Refresh Token是否在Redis中存在且有效（实现单点登录控制）
        if (!tokenValidator.isRefreshTokenValid(username, refreshClaims)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Refresh Token已失效，请重新登录");
        }

        // 将旧的Refresh Token加入黑名单
        tokenValidator.addToBlacklist(refreshClaims);

        // 生成新的双Token
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        // 将新的Refresh Token存储到Redis（替换旧的）
        tokenValidator.storeRefreshToken(username, newRefreshToken);

        return buildLoginVo(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(LogoutDto dto) {
        // 从请求上下文获取 Access Token 信息（网关已验证并透传）
        String accessTokenJti = UserContextUtils.getTokenJti();
        String accessTokenExp = UserContextUtils.getTokenExp();

        // 将Access Token加入黑名单
        if (StringUtils.hasText(accessTokenJti) && StringUtils.hasText(accessTokenExp)) {
            try {
                long expirationTime = Long.parseLong(accessTokenExp);
                tokenValidator.addToBlacklist(accessTokenJti, expirationTime, TokenType.ACCESS);
            } catch (NumberFormatException e) {
                // 过期时间格式错误，忽略Access Token黑名单操作
            }
        }

        // 解析并验证Refresh Token
        Claims refreshClaims = tokenValidator.parseAndValidate(dto.getRefreshToken(), TokenType.REFRESH);
        // 将Refresh Token加入黑名单
        tokenValidator.addToBlacklist(refreshClaims);

        // 从Token中获取用户名，如果存在则从Redis删除对应的Refresh Token
        // 实现单点登录控制，确保旧Refresh Token立即失效
        Optional.ofNullable(refreshClaims.get(JwtConstants.Claims.USERNAME))
                .map(Object::toString)
                .ifPresent(username -> tokenValidator.removeRefreshToken(username));
    }

    /**
     * 处理登录失败逻辑
     */
    private void handleLoginFailure(String username, SysUserEntity user) {
        // 增加失败次数
        loginSecurityUtils.incrementLoginAttempts(username);

        // 获取失败次数
        int attempts = loginSecurityUtils.getLoginAttempts(username);

        // 如果达到最大尝试次数，锁定账号
        if (attempts >= securityConfig.getMaxAttempts()) {

            // 锁定账号
            loginSecurityUtils.lockAccount(username);

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
        loginSecurityUtils.resetLoginAttempts(username);

        // 删除锁定状态
        loginSecurityUtils.clearAccountLock(username);
    }

    /**
     * 构建LoginVo对象
     */
    private LoginVo buildLoginVo(String accessToken, String refreshToken) {
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(accessToken);
        loginVo.setRefreshToken(refreshToken);
        loginVo.setAccessTokenExpiresIn(jwtUtil.getAccessTokenTtlSeconds());
        loginVo.setRefreshTokenExpiresIn(jwtUtil.getRefreshTokenTtlSeconds());
        return loginVo;
    }


}