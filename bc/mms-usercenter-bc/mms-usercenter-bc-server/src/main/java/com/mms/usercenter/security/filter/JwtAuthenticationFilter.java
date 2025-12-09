package com.mms.usercenter.security.filter;

/**
 * 实现功能【JWT 认证过滤器】
 * <p>
 * 从请求头提取用户信息（网关已透传）
 * 调用 UserDetailsService 加载用户权限
 * 创建 Authentication 对象
 * 放入 SecurityContext
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:42:40
 */
public class JwtAuthenticationFilter {

}