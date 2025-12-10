# 第 3 天教学讲义（约 20 分钟）

> 主题：数据库表结构与审查报告，导入初始化 SQL

## 今日目标
- 认识 10 张核心表（用户、角色、权限、部门、岗位等）。
- 知道索引/审计字段/逻辑删除的设计意图。
- 导入 `mysql/init_mms_dev_core.sql`。

## 节奏（20 分钟）
1. 5 分钟：看表清单与命名规则。
2. 10 分钟：挑 2-3 张关键表细看（用户/角色/权限）。
3. 5 分钟：导入 SQL 并验证。

## 关键资料
- SQL：`mysql/init_mms_dev_core.sql`（幂等，可多次执行）。
- 说明：`mysql/最终审查报告.md`（评分 5/5，讲了命名、索引、审计字段）。

## 讲解提纲
1) 命名与公共字段  
   - 表前缀 `sys_`，主键 `id bigint auto_increment`。  
   - 审计：`create_by/create_time/update_by/update_time`。  
   - 逻辑删除：`deleted tinyint`。
2) 关键表速览  
   - `sys_user`：用户名/密码(BCrypt)/状态/锁定/最后登录时间与 IP。  
   - `sys_role`、`sys_permission`、`sys_dept`、`sys_post`：各自 code 唯一索引。  
   - 关联表：用户-角色、角色-权限（若有）。  
3) 索引设计  
   - 唯一索引：username/email/phone/role_code/permission_code 等。  
   - 联合索引：`idx_status_deleted`、`idx_user_login_time` 等。  
4) 导入 SQL（动手）  
   - 在本地 MySQL 执行 `init_mms_dev_core.sql`。  
   - 验证：`select count(*) from sys_user;` 观察初始数据。

## 小练习（5 分钟内）
- 找出 `sys_user` 里与登录安全相关的字段（锁定、状态、审计）。  
- 思考：如果要做“登录审计表”，字段会有哪些？（预告 Day6）

## 课后巩固
- 阅读审查报告，确认自己理解了索引和审计字段的意义。  
- 若导入有报错，记录错误消息，明天一起解决。


