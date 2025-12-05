package com.mms.common.security.jwt;

/**
 * 实现功能【JWT常量类】
 * <p>
 * 统一管理JWT相关的常量定义
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-05 09:42:26
 */
public class JwtConstants {

    /**
     * JWT Claims中的用户名键
     */
    public static final String CLAIM_USERNAME = "username";

    /**
     * JWT Claims中的Token类型键
     */
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    /**
     * Redis中Token黑名单的key前缀
     */
    public static final String TOKEN_BLACKLIST_PREFIX = "mms:auth:blacklist:";

    /**
     * 私有构造函数，防止实例化
     */
    private JwtConstants() {
        throw new UnsupportedOperationException("常量类不允许实例化");
    }
}