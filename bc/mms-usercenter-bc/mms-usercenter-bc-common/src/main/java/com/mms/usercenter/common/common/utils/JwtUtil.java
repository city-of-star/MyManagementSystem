package com.mms.usercenter.common.common.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 实现功能【JWT 工具类】
 * <p>
 * 提供 JWT Token 的生成、解析、验证功能
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 15:34:26
 */
@Component
public class JwtUtil {

    /**
     * JWT 密钥（从 Nacos 配置中心读取）
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * JWT 过期时间（毫秒，从 Nacos 配置中心读取）
     */
    @Value("${jwt.expiration}")
    private Long expiration;

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * 生成 JWT Token
     *
     * @param username 用户账号
     * @return JWT Token
     */
    public String generateToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", username);

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从 Token 中解析 Claims
     *
     * @param token JWT Token
     * @return Claims（包含所有自定义字段，可通过 claims.get("key") 获取值）
     */
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * 验证 Token 是否有效（包括签名和过期时间）
     *
     * @param token JWT Token
     * @return 是否有效
     */
    public boolean validateToken(String token) {
        try {
            Claims claims = parseToken(token);
            return claims.getExpiration().after(new Date());
        } catch (Exception e) {
            return false;
        }
    }
}