package com.mms.common.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 实现功能【JWT 工具类：生成、解析、验证JWT】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
public class JwtUtil {

	private final JwtProperties jwtProperties;

	public JwtUtil(JwtProperties jwtProperties) {
		this.jwtProperties = jwtProperties;
	}

	private SecretKey getSigningKey() {
		byte[] keyBytes = jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8);
		return Keys.hmacShaKeyFor(keyBytes);
	}

	public String generateToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("username", username);

		Date now = new Date();
		long expiration = jwtProperties.getExpiration() != null ? jwtProperties.getExpiration() : 3600000L;
		Date expiryDate = new Date(now.getTime() + expiration);

		return Jwts.builder()
				.claims(claims)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}

	public Claims parseToken(String token) {
		return Jwts.parser()
				.verifyWith(getSigningKey())
				.build()
				.parseSignedClaims(token)
				.getPayload();
	}

	public boolean validateToken(String token) {
		try {
			Claims claims = parseToken(token);
			return claims.getExpiration().after(new Date());
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 生成Access Token
	 *
	 * @param username 用户名
	 * @return Access Token
	 */
	public String generateAccessToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(JwtConstants.CLAIM_USERNAME, username);
		claims.put(JwtConstants.CLAIM_TOKEN_TYPE, TokenType.ACCESS.name());

		Date now = new Date();
		long expiration = jwtProperties.getAccessExpiration() != null
				? jwtProperties.getAccessExpiration() : 900000L; // 默认15分钟
		Date expiryDate = new Date(now.getTime() + expiration);

		String jti = UUID.randomUUID().toString();

		return Jwts.builder()
				.id(jti)
				.claims(claims)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * 生成Refresh Token
	 *
	 * @param username 用户名
	 * @return Refresh Token
	 */
	public String generateRefreshToken(String username) {
		Map<String, Object> claims = new HashMap<>();
		claims.put(JwtConstants.CLAIM_USERNAME, username);
		claims.put(JwtConstants.CLAIM_TOKEN_TYPE, TokenType.REFRESH.name());

		Date now = new Date();
		long expiration = jwtProperties.getRefreshExpiration() != null
				? jwtProperties.getRefreshExpiration() : 604800000L; // 默认7天
		Date expiryDate = new Date(now.getTime() + expiration);

		String jti = UUID.randomUUID().toString();

		return Jwts.builder()
				.id(jti)
				.claims(claims)
				.issuedAt(now)
				.expiration(expiryDate)
				.signWith(getSigningKey())
				.compact();
	}

	/**
	 * 从Claims中提取Token类型
	 *
	 * @param claims JWT Claims
	 * @return Token类型，如果不存在则返回null
	 */
	public TokenType extractTokenType(Claims claims) {
		Object tokenTypeObj = claims.get(JwtConstants.CLAIM_TOKEN_TYPE);
		if (tokenTypeObj == null) {
			return null;
		}
		try {
			return TokenType.valueOf(tokenTypeObj.toString());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * 获取Access Token的TTL（秒数）
	 *
	 * @return TTL（秒）
	 */
	public long getAccessTokenTtlSeconds() {
		long expiration = jwtProperties.getAccessExpiration() != null
				? jwtProperties.getAccessExpiration() : 900000L;
		return expiration / 1000;
	}

	/**
	 * 获取Refresh Token的TTL（秒数）
	 *
	 * @return TTL（秒）
	 */
	public long getRefreshTokenTtlSeconds() {
		long expiration = jwtProperties.getRefreshExpiration() != null
				? jwtProperties.getRefreshExpiration() : 604800000L;
		return expiration / 1000;
	}
}


