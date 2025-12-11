package com.mms.common.security.config;

import com.mms.common.security.properties.JwtProperties;
import com.mms.common.security.utils.RefreshTokenUtils;
import com.mms.common.security.utils.TokenBlacklistUtils;
import com.mms.common.security.utils.JwtUtils;
import com.mms.common.security.utils.TokenValidatorUtils;
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
@ConditionalOnClass(Jwts.class)  // 只有在项目中引入了 jjwt 依赖时此配置类才生效
@EnableConfigurationProperties(JwtProperties.class)  // 在此类当中注入 JwtProperties Bean
public class CommonSecurityAutoConfiguration {

	/**
	 * 创建 JwtUtils Bean
	 * 只有当配置了 jwt.secret 属性的时候才创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnProperty(prefix = "jwt", name = "secret")
	public JwtUtils jwtUtils(JwtProperties jwtProperties) {
		return new JwtUtils(jwtProperties);
	}

	/**
	 * 创建 TokenBlacklistUtils Bean
	 * 只有当有 RedisTemplate 的时候才创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(RedisTemplate.class)
	public TokenBlacklistUtils tokenBlacklistUtils(RedisTemplate<String, Object> redisTemplate) {
		return new TokenBlacklistUtils(redisTemplate);
	}

	/**
	 * 创建 RefreshTokenUtils Bean
	 * 只有当有 RedisTemplate 的时候才创建
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean(RedisTemplate.class)
	public RefreshTokenUtils refreshTokenUtils(RedisTemplate<String, Object> redisTemplate) {
		return new RefreshTokenUtils(redisTemplate);
	}

	/**
	 * 创建 TokenValidatorUtils Bean
	 * 只有当 JwtUtils 存在时才创建（即配置了 jwt.secret）
	 * 只有当 TokenBlacklistUtils 存在时才创建（即配置了 RedisTemplate）
	 */
	@Bean
	@ConditionalOnMissingBean
	@ConditionalOnBean({JwtUtils.class, TokenBlacklistUtils.class})
	public TokenValidatorUtils tokenValidatorUtils(
			JwtUtils jwtUtils,
			TokenBlacklistUtils tokenBlacklistUtils) {
		return new TokenValidatorUtils(jwtUtils, tokenBlacklistUtils);
	}
}


