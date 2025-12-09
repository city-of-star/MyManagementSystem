# MMS 项目 7 天入门讲义（20 分钟/天）

面向：有基础的 Spring Boot 初学者  
目标：帮你快速理解并能跑起来当前项目（微服务 + 网关 + 登录）

## 目录
- 项目速览
- 每日学习计划（7 天，每天 20 分钟）
- 跑通项目步骤
- 关键概念白话
- 后续进阶练习

## 项目速览
- 架构：Spring Boot 3.2.4 + Spring Cloud 2023 + Spring Cloud Alibaba 2023 + MyBatis-Plus + Redis + MySQL + Nacos。
- 模块：
  - `mms-common-bc-*`：公共能力（工具、异常/返回体、Security、DB、WebMVC）。
  - `mms-usercenter-bc-*`：用户中心（登录、双 Token、黑名单、失败锁定、刷新、登出）。
  - `mms-gateway-bc`：网关（JWT 鉴权、白名单、TraceId、Client IP 透传、统一异常）。
  - `mms-base-bc-*`：基础数据（后续可扩展业务）。
- 数据库：`mysql/init_mms_dev_core.sql`（10 张表，用户、角色、权限、部门、岗位等）。
- 参考文档：`README.md`、`网关与登录服务最佳实践评估报告.md`、`mysql/最终审查报告.md`。

## 每日学习计划（7 天 x 20 分钟）
1) Day1：看 `README.md`，理解多模块结构；准备 JDK17、Maven、MySQL、Redis、Nacos。
2) Day2：浏览 `mms-common-bc` 下的 core/web-mvc/security/database，理解统一响应体、错误码、异常处理、日志与 TraceId。
3) Day3：阅读 `mysql/init_mms_dev_core.sql` 和 `最终审查报告.md`，导入表，理解字段/索引/审计字段。
4) Day4：看 `mms-usercenter-bc` 的 controller/service/common，跟踪登录流程：BCrypt、失败计数与锁定（Redis）、双 Token 生成、黑名单、刷新、登出；用 Postman/ApiFox 调一次 `/usercenter/auth/login`。
5) Day5：看 `mms-gateway-bc`，理解过滤器链（TraceFilter → ClientIpFilter → JwtAuthFilter）、白名单与 Header 透传（用户名、Token Jti、过期时间、客户端 IP、TraceId）。
6) Day6：对照《网关与登录服务最佳实践评估报告》理解现状与待办（限流/熔断、密码复杂度、登录审计等）。
7) Day7：综合演练：启动依赖 → 启动 usercenter 与 gateway → 登录并携带 Access Token 访问受保护接口；在日志中观察 TraceId 和透传 Header。

## 跑通项目步骤
1) 安装并启动：MySQL 8、Redis、Nacos；准备 JDK17、Maven。
2) 导入 SQL：执行 `mysql/init_mms_dev_core.sql`（可多次执行，幂等）。
3) Nacos 配置：按提交 `280123f` 的说明，确保 jwt/gateway 等配置在 Nacos（如 `jwt-DEV.yaml`）。
4) 启动顺序：Nacos/Redis/MySQL → `mms-usercenter-bc-server` → `mms-gateway-bc`（common/all 模块为依赖，无需单独跑）。
5) 调试路径：
   - 登录：`POST /usercenter/auth/login` → 返回 Access/Refresh Token。
   - 刷新：`POST /usercenter/auth/refresh`。
   - 登出：`POST /usercenter/auth/logout`（加入黑名单，刷新表删除）。
   - 携带 Access Token 访问受保护接口，查看网关透传的 Header 与日志中的 TraceId。

## 关键概念白话
- 微服务：像商场里的不同店铺；网关是入口安检 + 导览；公共模块是共享水电。
- 双 Token：短期票（Access）+ 续期票（Refresh）；黑名单是“拉黑失效票”。
- TraceId：给每次请求贴唯一编号，在日志里像“查快递单号”。
- Redis：记“登录失败次数”“黑名单”“Refresh Token 唯一性”，速度快。
- Nacos：配置大本营 + 服务通讯录。

## 后续进阶练习（可做小作业）
- 给登录加“密码复杂度校验”并落在注册/改密流程。
- 建“登录审计日志表”，记录用户、时间、IP、UA、结果。
- 在网关加基础限流（RedisRateLimiter）和熔断（CircuitBreaker/Resilience4j）。
- 接入 Zipkin/SkyWalking 体验链路追踪，把 TraceId 打通到可视化。

## 若时间更多，可补充
- 绘登录/鉴权时序图；整理网关白名单与 Header 约定文档。
- 把错误码写进 Swagger 描述，补一份《登录错误码说明》。

祝学习顺利，20 分钟/天也能稳步进阶！

