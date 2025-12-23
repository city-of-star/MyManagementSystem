package com.mms.common.core.constants.security;

/**
 * 实现功能【用户中心 Redis 缓存相关常量】
 * <p>
 * 统一管理 Key 前缀、TTL 等缓存约定
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-19 16:55:20
 */
public final class UserCenterCacheConstants {

    /**
     * 登录安全相关
     */
    public static final class LoginSecurity {

        /**
         * 登录失败次数缓存前缀
         * 示例：mms:usercenter:login:attempts:{username}
         */
        public static final String LOGIN_ATTEMPT_PREFIX = "mms:usercenter:login:attempts:";

        /**
         * 账号锁定状态缓存前缀
         * 示例：mms:usercenter:login:lock:{username}
         */
        public static final String ACCOUNT_LOCK_PREFIX = "mms:usercenter:login:lock:";

        private LoginSecurity() {
        }
    }

    /**
     * 用户权限相关
     */
    public static final class UserAuthority {

        /**
         * 用户角色集合缓存前缀
         * 示例：mms:usercenter:roles:{userId}
         */
        public static final String USER_ROLE_PREFIX = "mms:usercenter:roles:";

        /**
         * 用户权限集合缓存前缀
         * 示例：mms:usercenter:perms:{userId}
         */
        public static final String USER_PERMISSION_PREFIX = "mms:usercenter:perms:";

        /**
         * Spring Security 角色前缀
         */
        public static final String ROLE_PREFIX = "ROLE_";

        /**
         * 角色、权限缓存默认过期时间（分钟）
         */
        public static final long ROLE_PERMISSION_CACHE_TTL_MINUTES = 30L;

        private UserAuthority() {
        }
    }

    private UserCenterCacheConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}


