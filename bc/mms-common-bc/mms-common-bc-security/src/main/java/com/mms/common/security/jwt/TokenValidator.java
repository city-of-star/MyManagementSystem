package com.mms.common.security.jwt;

import com.mms.common.core.constants.gateway.GatewayConstants;
import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import io.jsonwebtoken.Claims;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * 实现功能【Token验证服务类】
 * <p>
 * 统一处理Token的解析、验证、黑名单检查等逻辑
 * 支持同步验证，适用于认证服务和网关等场景
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-08 10:18:37
 */
public class TokenValidator {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 构造函数
     *
     * @param jwtUtil JWT工具类
     */
    public TokenValidator(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = null;
    }

    /**
     * 构造函数（带Redis支持）
     *
     * @param jwtUtil       JWT工具类
     * @param redisTemplate Redis模板（用于黑名单检查，可选）
     */
    public TokenValidator(JwtUtil jwtUtil, RedisTemplate<String, Object> redisTemplate) {
        this.jwtUtil = jwtUtil;
        this.redisTemplate = redisTemplate;
    }

    /**
     * 解析并验证Token
     *
     * @param token        Token字符串
     * @param expectedType 期望的Token类型（可为null，表示不验证类型）
     * @return Claims
     * @throws BusinessException 验证失败时抛出
     */
    public Claims parseAndValidate(String token, TokenType expectedType) {
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
            if (expectedType != null) {
                TokenType realType = jwtUtil.extractTokenType(claims);
                if (realType != expectedType) {
                    throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token类型不匹配");
                }
            }

            // 检查Token是否在黑名单中（如果配置了Redis）
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
    public boolean isBlacklisted(String jti) {
        if (!StringUtils.hasText(jti) || redisTemplate == null) {
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
    public void addToBlacklist(Claims claims) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate未配置，无法使用黑名单功能");
        }

        if (claims == null) {
            return;
        }

        // 获取 jti、过期时间、token类型
        String jti = claims.getId();
        Date expiration = claims.getExpiration();
        if (!StringUtils.hasText(jti) || expiration == null) {
            return;
        }
        TokenType tokenType = jwtUtil.extractTokenType(claims);

        // 调用重载方法
        addToBlacklist(jti, expiration.getTime(), tokenType);
    }

    /**
     * 将Token加入黑名单（通过jti和过期时间）
     * <p>
     * 适用于网关已验证Token并透传jti和过期时间的场景
     * </p>
     *
     * @param jti        Token的唯一标识
     * @param expiration Token的过期时间戳（毫秒）
     * @param tokenType  Token类型（ACCESS或REFRESH）
     */
    public void addToBlacklist(String jti, long expiration, TokenType tokenType) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate未配置，无法使用黑名单功能");
        }

        if (!StringUtils.hasText(jti)) {
            return;
        }

        // 计算剩余有效时间
        long ttl = expiration - System.currentTimeMillis();
        if (ttl <= 0) {
            // Token已过期，无需加入黑名单
            return;
        }

        // 将Token加入黑名单，TTL设置为Token的剩余有效时间
        String key = JwtConstants.TOKEN_BLACKLIST_PREFIX + jti;
        redisTemplate.opsForValue().set(key, tokenType != null ? tokenType.name() : "ACCESS", ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 将Refresh Token存储到Redis
     * 使用用户名作为key，实现单点登录控制（一个用户只能有一个有效的refresh token）
     *
     * @param username      用户名
     * @param refreshToken  Refresh Token字符串
     */
    public void storeRefreshToken(String username, String refreshToken) {
        if (redisTemplate == null) {
            throw new IllegalStateException("RedisTemplate未配置，无法使用Refresh Token存储功能");
        }

        // 解析 Token，获取 refreshClaims
        Claims refreshClaims = jwtUtil.parseToken(refreshToken);

        if (!StringUtils.hasText(username) || !StringUtils.hasText(refreshToken) || refreshClaims == null) {
            return;
        }

        // 获取过期时间
        Date expiration = refreshClaims.getExpiration();
        if (expiration == null) {
            return;
        }

        // 计算剩余有效时间
        long ttl = expiration.getTime() - System.currentTimeMillis();
        if (ttl <= 0) {
            // Token已过期，无需存储
            return;
        }

        // 存储Refresh Token，key格式：mms:auth:refresh:{username}
        // value存储refresh token的jti，用于后续验证
        String key = JwtConstants.REFRESH_TOKEN_PREFIX + username;
        String jti = refreshClaims.getId();
        redisTemplate.opsForValue().set(key, jti != null ? jti : refreshToken, ttl, TimeUnit.MILLISECONDS);
    }

    /**
     * 从Redis获取Refresh Token的jti
     *
     * @param username 用户名
     * @return Refresh Token的jti，如果不存在则返回null
     */
    public String getRefreshTokenJti(String username) {
        if (redisTemplate == null || !StringUtils.hasText(username)) {
            return null;
        }

        String key = JwtConstants.REFRESH_TOKEN_PREFIX + username;
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * 验证Refresh Token是否有效（检查是否在Redis中存在且匹配）
     *
     * @param username      用户名
     * @param refreshClaims Refresh Token的Claims
     * @return true表示有效，false表示无效
     */
    public boolean isRefreshTokenValid(String username, Claims refreshClaims) {
        if (redisTemplate == null || !StringUtils.hasText(username) || refreshClaims == null) {
            return false;
        }

        String storedJti = getRefreshTokenJti(username);
        String currentJti = refreshClaims.getId();

        // 如果Redis中没有存储，或者jti不匹配，则认为无效
        return storedJti != null && storedJti.equals(currentJti);
    }

    /**
     * 从Redis删除Refresh Token
     *
     * @param username 用户名
     */
    public void removeRefreshToken(String username) {
        if (redisTemplate == null || !StringUtils.hasText(username)) {
            return;
        }

        String key = JwtConstants.REFRESH_TOKEN_PREFIX + username;
        redisTemplate.delete(key);
    }

    /**
     * 从Authorization请求头中提取Bearer Token
     * <p>
     * 支持格式：Authorization: Bearer &lt;token&gt;
     * </p>
     *
     * @param authHeader Authorization请求头的值
     * @return 提取的Token字符串，如果格式不正确或为空则返回null
     */
    public static String extractTokenFromHeader(String authHeader) {
        if (!StringUtils.hasText(authHeader)) {
            return null;
        }

        String bearerPrefix = GatewayConstants.Headers.BEARER_PREFIX;
        if (!authHeader.startsWith(bearerPrefix)) {
            return null;
        }

        String token = authHeader.substring(bearerPrefix.length()).trim();
        return StringUtils.hasText(token) ? token : null;
    }

    /**
     * 检查是否支持Refresh Token存储功能
     *
     * @return true表示支持，false表示不支持
     */
    public boolean isRefreshTokenStorageSupported() {
        return redisTemplate != null;
    }

    /**
     * 检查是否支持黑名单功能
     *
     * @return true表示支持，false表示不支持
     */
    public boolean isBlacklistSupported() {
        return redisTemplate != null;
    }
}

