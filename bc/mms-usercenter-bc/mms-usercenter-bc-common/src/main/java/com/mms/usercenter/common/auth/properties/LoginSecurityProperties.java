package com.mms.usercenter.common.auth.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 实现功能【登录安全配置属性】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-08 16:55:50
 */
@Data
@Component
@ConfigurationProperties(prefix = "login.security")
public class LoginSecurityProperties {
    /**
     * 最大登录失败次数
     */
    private int maxAttempts = 5;

    /**
     * 锁定时间（分钟）
     */
    private int lockTime = 30;

    /**
     * 登录失败次数缓存前缀
     */
    private String attemptKeyPrefix = "login_attempts:";

    /**
     * 账号锁定缓存前缀
     */
    private String lockKeyPrefix = "account_lock:";
}

