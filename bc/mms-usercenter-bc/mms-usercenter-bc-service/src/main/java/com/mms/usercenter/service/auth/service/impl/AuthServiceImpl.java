package com.mms.usercenter.service.auth.service.impl;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.security.jwt.JwtConstants;
import com.mms.common.security.jwt.JwtUtil;
import com.mms.common.security.jwt.TokenType;
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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
    private LoginSecurityUtils loginSecurityUtils;

    @Resource
    private LoginSecurityConfig securityConfig;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

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

            return buildLoginVo(accessToken, refreshToken);
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

    @Override
    public LoginVo refreshToken(RefreshTokenDto dto) {
        // 解析并验证Refresh Token
        Claims refreshClaims = parseAndValidate(dto.getRefreshToken(), TokenType.REFRESH);

        // 将旧的Refresh Token加入黑名单
        addToBlacklist(refreshClaims);

        // 提取用户名
        String username = Optional.ofNullable(refreshClaims.get(JwtConstants.CLAIM_USERNAME))
                .map(Object::toString)
                .orElseThrow(() -> new BusinessException(ErrorCode.INVALID_TOKEN, "Token中缺少用户名信息"));

        // 生成新的双Token
        String newAccessToken = jwtUtil.generateAccessToken(username);
        String newRefreshToken = jwtUtil.generateRefreshToken(username);

        return buildLoginVo(newAccessToken, newRefreshToken);
    }

    @Override
    public void logout(String accessToken, LogoutDto dto) {
        // 解析并验证Access Token
        Claims accessClaims = parseAndValidate(accessToken, TokenType.ACCESS);
        // 将Access Token加入黑名单
        addToBlacklist(accessClaims);

        // 解析并验证Refresh Token
        Claims refreshClaims = parseAndValidate(dto.getRefreshToken(), TokenType.REFRESH);
        // 将Refresh Token加入黑名单
        addToBlacklist(refreshClaims);
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

    /**
     * 解析并验证Token
     *
     * @param token         Token字符串
     * @param expectedType  期望的Token类型
     * @return Claims
     */
    private Claims parseAndValidate(String token, TokenType expectedType) {
        if (!StringUtils.hasText(token)) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token不能为空");
        }

        try {
            // 解析Token
            Claims claims = jwtUtil.parseToken(token);

            // 验证Token是否过期
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
            }

            // 验证Token类型
            TokenType realType = jwtUtil.extractTokenType(claims);
            if (expectedType != null && realType != expectedType) {
                throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token类型不匹配");
            }

            // 检查Token是否在黑名单中
            String jti = claims.getId();
            if (StringUtils.hasText(jti) && isBlacklisted(jti)) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED, "Token已失效");
            }

            return claims;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token解析失败");
        }
    }

    /**
     * 检查Token是否在黑名单中
     *
     * @param jti Token的唯一标识
     * @return true表示在黑名单中，false表示不在
     */
    private boolean isBlacklisted(String jti) {
        if (!StringUtils.hasText(jti)) {
            return false;
        }
        String key = JwtConstants.TOKEN_BLACKLIST_PREFIX + jti;
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * 将Token加入黑名单
     *
     * @param claims Token的Claims
     */
    private void addToBlacklist(Claims claims) {
        String jti = claims.getId();
        Date expiration = claims.getExpiration();

        if (!StringUtils.hasText(jti) || expiration == null) {
            return;
        }

        // 计算剩余有效时间
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl <= 0) {
            // Token已过期，无需加入黑名单
            return;
        }

        // 将Token加入黑名单，TTL设置为Token的剩余有效时间
        String key = JwtConstants.TOKEN_BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, claims.get(JwtConstants.CLAIM_TOKEN_TYPE), ttl, TimeUnit.MILLISECONDS);
    }

}