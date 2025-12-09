package com.mms.usercenter.common.security.constants;

/**
 * 实现功能【权限常量类】
 * <p>
 * 定义所有权限编码常量
 * 避免硬编码，统一管理权限编码
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:39:08
 */
public final class PermissionConstants {

    /**
     * 用户管理
     */
    public static final String USER_VIEW = "user:view";
    public static final String USER_CREATE = "user:create";
    public static final String USER_UPDATE = "user:update";
    public static final String USER_DELETE = "user:delete";
    public static final String USER_RESET_PASSWORD = "user:reset-password";
    public static final String USER_UNLOCK = "user:unlock";

    /**
     * 角色管理
     */
    public static final String ROLE_VIEW = "role:view";
    public static final String ROLE_CREATE = "role:create";
    public static final String ROLE_UPDATE = "role:update";
    public static final String ROLE_DELETE = "role:delete";
    public static final String ROLE_ASSIGN = "role:assign";

    /**
     * 权限/菜单管理
     */
    public static final String PERMISSION_VIEW = "permission:view";
    public static final String PERMISSION_CREATE = "permission:create";
    public static final String PERMISSION_UPDATE = "permission:update";
    public static final String PERMISSION_DELETE = "permission:delete";

    /**
     * 组织/岗位管理
     */
    public static final String DEPT_VIEW = "dept:view";
    public static final String DEPT_MANAGE = "dept:manage";
    public static final String POST_VIEW = "post:view";
    public static final String POST_MANAGE = "post:manage";

    /**
     * 系统审计
     */
    public static final String AUDIT_VIEW = "audit:view";
}