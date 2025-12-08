package com.mms.common.security.config;

import com.mms.common.security.jwt.JwtProperties;
import com.mms.common.security.jwt.JwtUtil;
import com.mms.common.security.jwt.TokenValidator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

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

	/**
	 * 创建TokenValidator Bean
	 * 只有当JwtUtil存在时才创建（即配置了jwt.secret）
	 * 如果存在RedisTemplate则使用带黑名单支持的版本，否则使用基础版本
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(JwtUtil.class)
	public TokenValidator tokenValidator(JwtUtil jwtUtil, 
	                                     @Autowired(required = false) RedisTemplate<String, Object> redisTemplate) {
		if (redisTemplate != null) {
			return new TokenValidator(jwtUtil, redisTemplate);
		}
		return new TokenValidator(jwtUtil);
	}
}


