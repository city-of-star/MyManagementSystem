package com.mms.common.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 实现功能【JWT 配置属性】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

	private String secret;

	private Long expiration;

	/**
	 * Access Token过期时间（毫秒），默认15分钟
	 */
	private Long accessExpiration = 900000L;

	/**
	 * Refresh Token过期时间（毫秒），默认7天
	 */
	private Long refreshExpiration = 604800000L;

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	public Long getExpiration() {
		return expiration;
	}

	public void setExpiration(Long expiration) {
		this.expiration = expiration;
	}

	public Long getAccessExpiration() {
		return accessExpiration;
	}

	public void setAccessExpiration(Long accessExpiration) {
		this.accessExpiration = accessExpiration;
	}

	public Long getRefreshExpiration() {
		return refreshExpiration;
	}

	public void setRefreshExpiration(Long refreshExpiration) {
		this.refreshExpiration = refreshExpiration;
	}
}


