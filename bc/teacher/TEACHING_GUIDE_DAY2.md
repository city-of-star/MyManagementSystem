# 第 2 天教学讲义（约 20 分钟）

> 主题：公共模块速通（统一响应体、异常处理、错误码、日志/TraceId、Security 基础）

## 今日目标
- 理解公共模块分层：`core`、`web-mvc`、`security`、`database`。
- 看懂统一响应体/异常/错误码的使用方式。
- 了解 TraceId 与日志的接入点。

## 节奏（20 分钟）
1. 5 分钟：模块地图 & 入口文件。
2. 10 分钟：代码走读（响应体、异常、错误码、TraceId）。
3. 5 分钟：提问/小练习。

## 快速索引（IDE 中定位）
- `mms-common-bc-core`：工具、上下文、常量。
- `mms-common-bc-web-mvc`：统一响应体/异常处理/Swagger。
- `mms-common-bc-security`：JWT 工具、Token 校验、Security 基础。
- `mms-common-bc-database`：MyBatis-Plus/数据源配置。

## 讲解提纲
1) 统一响应体  
   - 看 `Response` 类（常用字段 code/msg/data）。  
   - 示范：Controller 返回 `Response.success(data)` 的写法。
2) 统一异常与错误码  
   - 看 `GlobalExceptionHandler`（处理校验异常/业务异常）。  
   - 看 `ErrorCode` 枚举的分段（1xxx 用户、2xxx 权限等）。  
   - 练习：在脑中过一遍登录失败会返回哪个错误码。
3) 日志与 TraceId  
   - 了解 TraceId 工具/过滤器（后续网关会用）。  
   - 说明：日志里会带 TraceId，方便排查。
4) Security 基础与 JWT 工具  
   - `JwtUtil` 位置：common-security。  
   - Token 生成/解析的公共入口；与网关/用户中心共享。
5) 数据库配置  
   - MyBatis-Plus 自动配置与分页插件（知道有即可）。

## 动手小练习（可 5 分钟）
- 打开一个 Controller，观察它如何返回 `Response`。  
- 思考：如果抛出 `BusinessException`，全局异常会怎么包装返回？

## 课后巩固
- 标记看不懂的类名/注解，明天进入登录服务时顺带解释。  
- 了解 ErrorCode 分段：记住 1000 段是用户相关。


