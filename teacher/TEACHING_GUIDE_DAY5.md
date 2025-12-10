# 第 5 天教学讲义（约 20 分钟）

> 主题：网关（Spring Cloud Gateway）过滤器链、白名单、Header 透传、统一异常

## 今日目标
- 了解网关在架构中的角色：入口安检 + 路由转发 + 统一鉴权。
- 看懂过滤器链：TraceId → ClientIp → JwtAuth。
- 知道网关如何白名单放行、如何校验 Token 并透传关键信息。

## 节奏（20 分钟）
1. 5 分钟：网关职责与配置入口。
2. 10 分钟：过滤器链走读。
3. 5 分钟：尝试带 Token 调一个后端接口。

## 关键位置
- `mms-gateway-bc/src/main/resources/application.yml`：路由/白名单/基础配置。
- 过滤器：`TraceFilter`、`ClientIpFilter`、`JwtAuthFilter`。
- 全局异常：WebFlux 全局异常处理器。
- 常量：`GatewayConstants`、`JwtConstants`。

## 讲解提纲
1) 配置与路由  
   - 路由规则：`spring.cloud.gateway.routes`。  
   - 白名单：允许跳过鉴权的路径（如登录、健康检查）。
2) 过滤器链顺序  
   - `TraceFilter`：生成/透传 TraceId，写入 Header（`X-Trace-Id`）和日志 MDC。  
   - `ClientIpFilter`：提取真实客户端 IP，写入 `X-Client-Ip`。  
   - `JwtAuthFilter`：白名单检查 → Token 校验 → 透传认证上下文。
3) Token 校验与透传  
   - 校验项：签名、有效期、类型、黑名单、Jti。  
   - 透传 Header（当前已实现）：`X-User-Name`、`X-Token-Jti`、`X-Token-Exp`、`X-Client-Ip`、`X-Trace-Id`。  
   - 透传目的：下游服务无需重复解析 Token。
4) 全局异常  
   - 超时/下游不可用/路由未找到/其他异常 → 统一响应体。  
   - 方便前端/客户端一致处理。

## 实操（5 分钟）
- 启动网关 + 用户中心，调用登录获取 Access Token。  
- 携带 Access Token 访问一个受保护路由，观察返回/日志里的 TraceId 与透传 Header。

## 小练习
- 问她：如果要加限流或熔断，应该配置在哪？（答：网关路由配置或过滤器，后续 Day6/Day7 扩展）

## 课后巩固
- 画一条请求链：客户端 → 网关过滤器（Trace/IP/JWT） → 下游服务。  
- 标记不懂的配置项，下一天集中答疑。


