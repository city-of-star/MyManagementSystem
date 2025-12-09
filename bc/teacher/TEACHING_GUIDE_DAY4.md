# 第 4 天教学讲义（约 20 分钟）

> 主题：用户中心登录流程（双 Token、黑名单、失败锁定、刷新、登出）

## 今日目标
- 走通登录主流程：校验账号 → BCrypt 校验密码 → 失败计数与锁定 → 生成 Access/Refresh Token。
- 了解黑名单、刷新、登出的用途和触发点。
- 用 ApiFox/Postman 调一次登录接口。

## 节奏（20 分钟）
1. 5 分钟：接口入口与分层。
2. 10 分钟：代码走读核心路径。
3. 5 分钟：实操一次登录。

## 关键位置（IDE 导航）
- Controller：`mms-usercenter-bc-controller` → `AuthController`（登录/刷新/登出）。
- Service：`mms-usercenter-bc-service` → `AuthService`/`AuthServiceImpl`。
- Common：`mms-usercenter-bc-common` → DTO/VO/实体、错误码。
- Security 公共能力：`mms-common-bc-security`（`JwtUtil`、`TokenValidator`）。

## 讲解提纲
1) 登录入口  
   - `POST /usercenter/auth/login`，请求 DTO：用户名 + 密码。  
   - 校验：空值校验 → 查询用户 → 状态/锁定检查 → BCrypt 校验密码。
2) 登录失败计数与锁定（Redis）  
   - 失败次数 +1，超阈值锁定；返回剩余次数或锁定时间。  
   - 提示：这用到了 Redis key 前缀（attempt/lock）。
3) 双 Token 生成  
   - Access Token（短期）+ Refresh Token（长期）。  
   - Refresh Token 写入 Redis，保证“单点登录”（新登录会顶掉旧的）。
4) 黑名单与刷新/登出  
   - 刷新：`/usercenter/auth/refresh` → 验证 Refresh Token → 生成新 Token → 旧 Token 入黑名单。  
   - 登出：`/usercenter/auth/logout` → Access/Refresh Token 全部入黑名单 → 删除 Redis Refresh 记录。  
   - 黑名单 key：`mms:auth:blacklist:{jti}`，TTL = 剩余有效期。
5) 错误码与提示  
   - 登录失败、账号锁定、账号禁用、Token 失效等对应的 `ErrorCode`。

## 实操（5 分钟）
- 用 ApiFox/Postman：`POST /usercenter/auth/login`，查看返回的 Access/Refresh Token。  
- 携带 Access Token 调一个受保护接口（可选，看网关配置），观察返回是否需要鉴权。

## 小练习
- 让她描述：刷新 Token 时旧 Token 去哪里？（答：入黑名单）  
- 思考：如果要支持密码复杂度校验，应加在何处？（预告 Day6）

## 课后巩固
- 复述登录 4 步：查用户→状态锁定→密码校验→发双 Token。  
- 记录任何看不懂的注解/工具类，Day5/Day6 继续解释。


