# 第 7 天教学讲义（约 20 分钟）

> 主题：综合演练 & 小练习落地（任选：密码复杂度 / 登录审计 / 网关限流）

## 今日目标
- 启动完整链路：Nacos + Redis + MySQL → 用户中心 → 网关。
- 走一次登录→鉴权→访问受保护接口→观察 TraceId/透传 Header。
- 动手实现/预演一个改进点（选一：密码复杂度、登录审计、限流）。

## 节奏（20 分钟）
1. 5 分钟：启动与健康检查。
2. 10 分钟：完整调用链 & 选择一个小练习。
3. 5 分钟：总结与后续路线。

## 启动检查
- 确保 Nacos/Redis/MySQL 已启动；Nacos 内有 jwt/gateway 配置。
- 启动 `mms-usercenter-bc-server`，再启动 `mms-gateway-bc`。
- 可选：检查日志里是否打印 TraceId，端口正常监听。

## 调用链演练
1) 登录：`POST /usercenter/auth/login` → 得到 Access/Refresh Token。  
2) 刷新：`POST /usercenter/auth/refresh`（可选）。  
3) 受保护接口：携带 Access Token 访问，通过网关，观察响应与日志。  
4) 登出：`POST /usercenter/auth/logout`，验证黑名单生效（旧 Token 再访问应被拒）。

## 选择一个小练习（示例思路）
1) 密码复杂度（最易上手）  
   - 在注册/改密逻辑前加校验：长度≥8，含大小写字母+数字+特殊字符，不满足抛 `PWD_WEAK`。  
   - 写 1-2 个单元/集成用例验证。
2) 登录审计  
   - 建表 `login_audit_log`，在登录成功/失败处记录（user、result、ip、ua、time、reason）。  
   - 先同步落库，后续可异步优化。
3) 网关限流  
   - 在 `application.yml` 为某个路由配置 `RedisRateLimiter`（如 5 req/s），验证超限返回。

## 总结与后续
- 你已具备：跑通项目、理解登录与网关、能做小改进。  
- 后续路线（按兴趣）：  
  - 安全：完善密码策略、二次验证、审计报表。  
  - 网关治理：限流/熔断/重试、灰度发布。  
  - 可观测：接入 Zipkin/SkyWalking、指标与告警。  
  - 权限：完善 RBAC/角色/权限透传，丰富 UserContext。

## 课后反思
- 让她复述：登录全链路 + 网关过滤器顺序 + 透传 Header。  
- 记录动手遇到的阻塞点，下一步逐个解决。


