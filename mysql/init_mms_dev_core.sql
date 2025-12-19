-- 创建 mms_dev_core 数据库
CREATE DATABASE IF NOT EXISTS `mms_dev_core` CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

-- 使用该数据库
USE `mms_dev_core`;

-- ==================== test 表 ====================

CREATE TABLE IF NOT EXISTS test
(
    id          bigint auto_increment comment '主键'
        primary key,
    title       varchar(512) not null default '' comment '测试标题',
    content     text comment '测试内容',
    create_time datetime     not null default CURRENT_TIMESTAMP comment '创建时间',
    update_time datetime     not null default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP comment '更新时间',
    INDEX idx_title (title),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='测试表-用于测试服务基础功能';

-- ==================== 用户中心服务相关表 ====================

-- 1. 用户表
CREATE TABLE IF NOT EXISTS `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` varchar(64) NOT NULL COMMENT '用户名（登录账号）',
    `password` varchar(255) NOT NULL COMMENT '密码（加密后）',
    `nickname` varchar(64) DEFAULT NULL COMMENT '昵称',
    `real_name` varchar(64) DEFAULT NULL COMMENT '真实姓名',
    `avatar` varchar(1024) DEFAULT NULL COMMENT '头像URL',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱（可为空，但填写后必须唯一）',
    `phone` varchar(32) DEFAULT NULL COMMENT '手机号（可为空，但填写后必须唯一）',
    `gender` tinyint DEFAULT 0 COMMENT '性别：0-未知，1-男，2-女',
    `birthday` date DEFAULT NULL COMMENT '生日',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `locked` tinyint NOT NULL DEFAULT 0 COMMENT '是否锁定：0-未锁定，1-已锁定',
    `lock_time` datetime DEFAULT NULL COMMENT '锁定时间',
    `lock_reason` varchar(255) DEFAULT NULL COMMENT '锁定原因',
    `last_login_time` datetime DEFAULT NULL COMMENT '最后登录时间',
    `last_login_ip` varchar(64) DEFAULT NULL COMMENT '最后登录IP',
    `password_update_time` datetime DEFAULT NULL COMMENT '密码更新时间',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    UNIQUE KEY `uk_email` (`email`),
    UNIQUE KEY `uk_phone` (`phone`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_create_time` (`create_time`),
    KEY `idx_status_deleted` (`status`, `deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- 2. 用户登录日志表
CREATE TABLE IF NOT EXISTS `user_login_log` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '日志ID',
    `user_id` bigint DEFAULT NULL COMMENT '用户ID',
    `username` varchar(64) DEFAULT NULL COMMENT '用户名',
    `login_type` varchar(32) DEFAULT NULL COMMENT '登录类型：password-密码登录，sms-短信登录，email-邮箱登录',
    `login_ip` varchar(64) DEFAULT NULL COMMENT '登录IP',
    `login_location` varchar(128) DEFAULT NULL COMMENT '登录地点',
    `user_agent` text DEFAULT NULL COMMENT '用户代理（浏览器信息）',
    `login_status` tinyint NOT NULL DEFAULT 0 COMMENT '登录状态：0-失败，1-成功',
    `login_message` varchar(255) DEFAULT NULL COMMENT '登录消息（失败原因等）',
    `login_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_username` (`username`),
    KEY `idx_login_time` (`login_time`),
    KEY `idx_login_status` (`login_status`),
    KEY `idx_user_login_time` (`user_id`, `login_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户登录日志表';

-- ==================== 组织权限服务相关表 ====================

-- 3. 部门表
CREATE TABLE IF NOT EXISTS `dept` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父部门ID，0表示顶级部门',
    `dept_name` varchar(64) NOT NULL COMMENT '部门名称',
    `dept_code` varchar(64) NOT NULL COMMENT '部门编码',
    `leader` varchar(64) DEFAULT NULL COMMENT '负责人',
    `phone` varchar(32) DEFAULT NULL COMMENT '联系电话',
    `email` varchar(128) DEFAULT NULL COMMENT '邮箱',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_dept_code` (`dept_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='部门表';

-- 4. 岗位表
CREATE TABLE IF NOT EXISTS `post` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '岗位ID',
    `post_code` varchar(64) NOT NULL COMMENT '岗位编码',
    `post_name` varchar(64) NOT NULL COMMENT '岗位名称',
    `post_level` varchar(32) DEFAULT NULL COMMENT '岗位等级',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_post_code` (`post_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='岗位表';

-- 5. 角色表
CREATE TABLE IF NOT EXISTS `role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    `role_code` varchar(64) NOT NULL COMMENT '角色编码',
    `role_name` varchar(64) NOT NULL COMMENT '角色名称',
    `role_type` varchar(32) DEFAULT NULL COMMENT '角色类型：system-系统角色，custom-自定义角色',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_code` (`role_code`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色表';

-- 6. 权限表（菜单/按钮/接口权限）
CREATE TABLE IF NOT EXISTS `permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '权限ID',
    `parent_id` bigint NOT NULL DEFAULT 0 COMMENT '父权限ID，0表示顶级权限',
    `permission_type` varchar(32) NOT NULL COMMENT '权限类型：menu-菜单，button-按钮，api-接口',
    `permission_name` varchar(64) NOT NULL COMMENT '权限名称',
    `permission_code` varchar(128) NOT NULL COMMENT '权限编码（唯一标识）',
    `path` varchar(255) DEFAULT NULL COMMENT '路由路径（菜单类型使用）',
    `component` varchar(255) DEFAULT NULL COMMENT '组件路径（菜单类型使用）',
    `icon` varchar(64) DEFAULT NULL COMMENT '图标（菜单类型使用）',
    `api_url` varchar(255) DEFAULT NULL COMMENT '接口URL（接口类型使用）',
    `api_method` varchar(16) DEFAULT NULL COMMENT '接口请求方式：GET,POST,PUT,DELETE等（接口类型使用）',
    `sort_order` int NOT NULL DEFAULT 0 COMMENT '排序号',
    `visible` tinyint NOT NULL DEFAULT 1 COMMENT '是否显示：0-隐藏，1-显示',
    `status` tinyint NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-启用',
    `remark` varchar(512) DEFAULT NULL COMMENT '备注',
    `deleted` tinyint NOT NULL DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_by` bigint DEFAULT NULL COMMENT '更新人ID',
    `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_permission_code` (`permission_code`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_permission_type` (`permission_type`),
    KEY `idx_status` (`status`),
    KEY `idx_deleted` (`deleted`),
    KEY `idx_status_deleted_type` (`status`, `deleted`, `permission_type`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限表';

-- 7. 用户角色关联表
CREATE TABLE IF NOT EXISTS `user_role` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_role_id` (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户角色关联表';

-- 8. 角色权限关联表
CREATE TABLE IF NOT EXISTS `role_permission` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `role_id` bigint NOT NULL COMMENT '角色ID',
    `permission_id` bigint NOT NULL COMMENT '权限ID',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_role_permission` (`role_id`, `permission_id`),
    KEY `idx_role_id` (`role_id`),
    KEY `idx_permission_id` (`permission_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色权限关联表';

-- 9. 用户部门关联表
CREATE TABLE IF NOT EXISTS `user_dept` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `dept_id` bigint NOT NULL COMMENT '部门ID',
    `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主部门：0-否，1-是',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_dept` (`user_id`, `dept_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_dept_id` (`dept_id`),
    KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户部门关联表';

-- 10. 用户岗位关联表
CREATE TABLE IF NOT EXISTS `user_post` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `user_id` bigint NOT NULL COMMENT '用户ID',
    `post_id` bigint NOT NULL COMMENT '岗位ID',
    `is_primary` tinyint NOT NULL DEFAULT 0 COMMENT '是否主岗位：0-否，1-是',
    `create_by` bigint DEFAULT NULL COMMENT '创建人ID',
    `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_post` (`user_id`, `post_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_post_id` (`post_id`),
    KEY `idx_is_primary` (`is_primary`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户岗位关联表';

-- ==================== 初始化数据 ====================

-- 初始化超级管理员用户（密码：admin123）
INSERT IGNORE INTO `user` (`id`, `username`, `password`, `nickname`, `real_name`, `status`, `locked`, `deleted`, `create_time`, `update_time`)
VALUES (1, 'admin', '$2a$10$cU5acgjEYlHA.2cql1DmiOVcTKexIR0iKpKAIda0gJyLdKxeE8Lt.', '超级管理员', '超级管理员', 1, 0, 0, NOW(), NOW());

-- 初始化系统管理员角色
INSERT IGNORE INTO `role` (`id`, `role_code`, `role_name`, `role_type`, `status`, `deleted`, `create_time`, `update_time`)
VALUES (1, 'admin', '超级管理员', 'system', 1, 0, NOW(), NOW());

-- 初始化普通用户角色
INSERT IGNORE INTO `role` (`id`, `role_code`, `role_name`, `role_type`, `status`, `deleted`, `create_time`, `update_time`)
VALUES (2, 'user', '普通用户', 'system', 1, 0, NOW(), NOW());

-- 给超级管理员分配角色
INSERT IGNORE INTO `user_role` (`user_id`, `role_id`, `create_time`)
VALUES (1, 1, NOW());

-- 初始化权限数据
INSERT IGNORE INTO `permission` (`id`, `parent_id`, `permission_type`, `permission_name`, `permission_code`,
                                 `sort_order`, `visible`, `status`, `deleted`, `create_time`, `update_time`)
VALUES
    -- 用户管理
    (1, 0, 'button', '用户-查看', 'user:view', 10, 1, 1, 0, NOW(), NOW()),
    (2, 0, 'button', '用户-新增', 'user:create', 11, 1, 1, 0, NOW(), NOW()),
    (3, 0, 'button', '用户-编辑', 'user:update', 12, 1, 1, 0, NOW(), NOW()),
    (4, 0, 'button', '用户-删除', 'user:delete', 13, 1, 1, 0, NOW(), NOW()),
    (5, 0, 'button', '用户-重置密码', 'user:reset-password', 14, 1, 1, 0, NOW(), NOW()),
    (6, 0, 'button', '用户-解锁', 'user:unlock', 15, 1, 1, 0, NOW(), NOW()),

    -- 角色管理
    (7, 0, 'button', '角色-查看', 'role:view', 20, 1, 1, 0, NOW(), NOW()),
    (8, 0, 'button', '角色-新增', 'role:create', 21, 1, 1, 0, NOW(), NOW()),
    (9, 0, 'button', '角色-编辑', 'role:update', 22, 1, 1, 0, NOW(), NOW()),
    (10, 0, 'button', '角色-删除', 'role:delete', 23, 1, 1, 0, NOW(), NOW()),
    (11, 0, 'button', '角色-分配权限', 'role:assign', 24, 1, 1, 0, NOW(), NOW()),

    -- 权限管理
    (12, 0, 'button', '权限-查看', 'permission:view', 30, 1, 1, 0, NOW(), NOW()),
    (13, 0, 'button', '权限-新增', 'permission:create', 31, 1, 1, 0, NOW(), NOW()),
    (14, 0, 'button', '权限-编辑', 'permission:update', 32, 1, 1, 0, NOW(), NOW()),
    (15, 0, 'button', '权限-删除', 'permission:delete', 33, 1, 1, 0, NOW(), NOW());

-- 将所有初始化的权限授予超级管理员角色（role_id = 1）
INSERT IGNORE INTO `role_permission` (`role_id`, `permission_id`, `create_time`)
VALUES
    (1, 1, NOW()),
    (1, 2, NOW()),
    (1, 3, NOW()),
    (1, 4, NOW()),
    (1, 5, NOW()),
    (1, 6, NOW()),
    (1, 7, NOW()),
    (1, 8, NOW()),
    (1, 9, NOW()),
    (1, 10, NOW()),
    (1, 11, NOW()),
    (1, 12, NOW()),
    (1, 13, NOW()),
    (1, 14, NOW()),
    (1, 15, NOW());