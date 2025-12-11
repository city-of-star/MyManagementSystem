package com.mms.common.security.utils;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.security.constants.JwtConstants;
import com.mms.common.security.enums.TokenType;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.Date;

/**
 * 实现功能【Token验证工具类】
 * <p>
 * 负责Token的解析、验证（签名、过期、类型）
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-08 10:18:37
 */
@AllArgsConstructor
public class TokenValidatorUtils {

    private final JwtUtils jwtUtils;
    private final TokenBlacklistUtils tokenBlacklistUtils;

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
            Claims claims = jwtUtils.parseToken(token);

            // 验证Token是否过期
            Date expiration = claims.getExpiration();
            if (expiration == null || expiration.before(new Date())) {
                throw new BusinessException(ErrorCode.LOGIN_EXPIRED);
            }

            // 验证Token类型
            if (expectedType != null) {
                TokenType realType = jwtUtils.extractTokenType(claims);
                if (realType != expectedType) {
                    throw new BusinessException(ErrorCode.INVALID_TOKEN, "Token类型不匹配");
                }
            }

            // 检查Token是否在黑名单中
            String jti = claims.getId();
            if (StringUtils.hasText(jti) &&  tokenBlacklistUtils.isBlacklisted(jti)) {
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

        if (!authHeader.startsWith(JwtConstants.Headers.BEARER_PREFIX)) {
            return null;
        }

        String token = authHeader.substring(JwtConstants.Headers.BEARER_PREFIX.length()).trim();
        return StringUtils.hasText(token) ? token : null;
    }
}
