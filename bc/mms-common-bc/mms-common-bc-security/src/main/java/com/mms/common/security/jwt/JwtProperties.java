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
}


