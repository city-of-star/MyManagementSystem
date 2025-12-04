package com.mms.common.security.config;

import com.mms.common.security.jwt.JwtProperties;
import com.mms.common.security.jwt.JwtUtil;
import io.jsonwebtoken.Jwts;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 实现功能【JWT 自动装配配置】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-04 15:46:51
 */
@Configuration
@EnableConfigurationProperties(JwtProperties.class)
@ConditionalOnClass(Jwts.class)
public class CommonSecurityAutoConfiguration {

	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "jwt", name = "secret")
	public JwtUtil jwtUtil(JwtProperties jwtProperties) {
		return new JwtUtil(jwtProperties);
	}
}


