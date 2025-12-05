# 双Token和Redis黑名单改造清单

## 📋 改造目标
1. 实现双Token机制（Access Token + Refresh Token）
2. 实现基于Redis的Token黑名单功能
3. 支持Token刷新接口
4. 支持登出时将Token加入黑名单

---

## 🗂️ 一、需要创建的新文件

### 1.1 Token类型枚举
**文件路径：** `mms-common-bc/mms-common-bc-security/src/main/java/com/mms/common/security/jwt/TokenType.java`

**内容要点：**
- 创建枚举类，包含两个值：`ACCESS`、`REFRESH`
- 用于区分Token类型

### 1.2 JWT常量类
**文件路径：** `mms-common-bc/mms-common-bc-security/src/main/java/com/mms/common/security/jwt/JwtConstants.java`

**内容要点：**
- 定义常量：
  - `CLAIM_USERNAME = "username"` - 用户名claim键
  - `CLAIM_TOKEN_TYPE = "tokenType"` - Token类型claim键
  - `TOKEN_BLACKLIST_PREFIX = "mms:auth:blacklist:"` - Redis黑名单key前缀
- 使用 `public static final String` 定义

### 1.3 刷新Token请求DTO
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-common/src/main/java/com/mms/usercenter/common/auth/dto/RefreshTokenDto.java`

**内容要点：**
- 字段：`refreshToken` (String)
- 添加 `@Valid` 和 `@NotNull` 验证注解
- 使用 Lombok `@Data`

### 1.4 登出请求DTO
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-common/src/main/java/com/mms/usercenter/common/auth/dto/LogoutDto.java`

**内容要点：**
- 字段：`refreshToken` (String)
- 添加 `@Valid` 和 `@NotNull` 验证注解
- 使用 Lombok `@Data`

---

## ✏️ 二、需要修改的现有文件

### 2.1 JwtProperties（JWT配置属性类）
**文件路径：** `mms-common-bc/mms-common-bc-security/src/main/java/com/mms/common/security/jwt/JwtProperties.java`

**修改内容：**
1. 新增字段：
   - `private Long accessExpiration;` - Access Token过期时间（毫秒），默认15分钟
   - `private Long refreshExpiration;` - Refresh Token过期时间（毫秒），默认7天
2. 保留原有字段：
   - `secret` - 保持不变
   - `expiration` - 保留作为兼容（可选，建议保留）
3. 添加对应的 getter/setter 方法

**配置示例（application.yml）：**
```yaml
jwt:
  secret: your-secret-key
  access-expiration: 900000    # 15分钟 = 15 * 60 * 1000
  refresh-expiration: 604800000 # 7天 = 7 * 24 * 60 * 60 * 1000
```

### 2.2 JwtUtil（JWT工具类）
**文件路径：** `mms-common-bc/mms-common-bc-security/src/main/java/com/mms/common/security/jwt/JwtUtil.java`

**修改内容：**

1. **新增方法：`generateAccessToken(String username)`**
   - 生成Access Token
   - 过期时间使用 `jwtProperties.getAccessExpiration()`，默认15分钟
   - 在claims中添加：
     - `"username"` = username
     - `"tokenType"` = `TokenType.ACCESS.name()`
     - `"jti"` = UUID（使用 `Jwts.builder().id(UUID.randomUUID().toString())`）

2. **新增方法：`generateRefreshToken(String username)`**
   - 生成Refresh Token
   - 过期时间使用 `jwtProperties.getRefreshExpiration()`，默认7天
   - 在claims中添加：
     - `"username"` = username
     - `"tokenType"` = `TokenType.REFRESH.name()`
     - `"jti"` = UUID

3. **保留原方法：`generateToken(String username)`**
   - 保持向后兼容，可以调用 `generateAccessToken` 或标记为 `@Deprecated`

4. **新增方法：`extractTokenType(Claims claims)`**
   - 从Claims中提取tokenType
   - 返回 `TokenType` 枚举
   - 如果不存在或解析失败，返回 `null` 或抛出异常

5. **新增方法：`getAccessTokenTtlSeconds()`**
   - 返回Access Token的TTL（秒数）
   - 用于前端显示过期时间

6. **新增方法：`getRefreshTokenTtlSeconds()`**
   - 返回Refresh Token的TTL（秒数）

**注意事项：**
- 使用 `java.util.UUID.randomUUID().toString()` 生成 jti
- 使用 `Jwts.builder().id(jti)` 设置 jti
- 使用 `claims.getId()` 获取 jti

### 2.3 LoginVo（登录返回VO）
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-common/src/main/java/com/mms/usercenter/common/auth/vo/LoginVo.java`

**修改内容：**
1. 将 `token` 字段改为：
   - `accessToken` (String) - 访问令牌
   - `refreshToken` (String) - 刷新令牌
   - `accessTokenExpiresIn` (Long) - Access Token过期时间（秒）
   - `refreshTokenExpiresIn` (Long) - Refresh Token过期时间（秒）

2. 可选：保留 `token` 字段并标记为 `@Deprecated`，用于兼容旧版本前端

### 2.4 AuthService（认证服务接口）
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-service/src/main/java/com/mms/usercenter/service/auth/service/AuthService.java`

**修改内容：**
1. 新增方法：`LoginVo refreshToken(RefreshTokenDto dto)`
   - 刷新Token接口

2. 新增方法：`void logout(String accessToken, LogoutDto dto)`
   - 登出接口
   - accessToken从请求头中提取（在Controller层处理）

### 2.5 AuthServiceImpl（认证服务实现类）
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-service/src/main/java/com/mms/usercenter/service/auth/service/impl/AuthServiceImpl.java`

**修改内容：**

1. **注入RedisTemplate**
   ```java
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   ```

2. **修改 `login` 方法：**
   - 将 `jwtUtil.generateToken()` 改为：
     - `jwtUtil.generateAccessToken(username)`
     - `jwtUtil.generateRefreshToken(username)`
   - 返回包含两个Token的 `LoginVo`

3. **新增 `refreshToken` 方法实现：**
   - 解析并验证Refresh Token（调用 `parseAndValidate`）
   - 验证Token类型必须是 `REFRESH`
   - 将旧的Refresh Token加入黑名单（调用 `addToBlacklist`）
   - 生成新的Access Token和Refresh Token
   - 返回新的Token

4. **新增 `logout` 方法实现：**
   - 解析并验证Access Token（调用 `parseAndValidate`）
   - 将Access Token加入黑名单
   - 解析并验证Refresh Token（调用 `parseAndValidate`）
   - 将Refresh Token加入黑名单

5. **新增私有辅助方法：`parseAndValidate(String token, TokenType expectedType)`**
   - 解析Token
   - 验证Token是否过期
   - 验证Token类型是否匹配
   - 检查Token是否在黑名单中
   - 返回 `Claims`
   - 如果验证失败，抛出 `BusinessException`

6. **新增私有辅助方法：`isBlacklisted(String jti)`**
   - 检查jti是否在Redis黑名单中
   - Redis key格式：`mms:auth:blacklist:{jti}`
   - 返回 `boolean`

7. **新增私有辅助方法：`addToBlacklist(Claims claims)`**
   - 将Token的jti加入Redis黑名单
   - Redis key格式：`mms:auth:blacklist:{jti}`
   - 设置TTL为Token的剩余有效时间（毫秒）
   - 如果jti为空或已过期，直接返回

**注意事项：**
- 使用 `JwtConstants.TOKEN_BLACKLIST_PREFIX` 作为Redis key前缀
- 使用 `claims.getExpiration().getTime() - System.currentTimeMillis()` 计算剩余TTL
- 使用 `redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS)` 设置黑名单

### 2.6 AuthController（认证控制器）
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-controller/src/main/java/com/mms/usercenter/controller/auth/AuthController.java`

**修改内容：**

1. **新增刷新Token接口：**
   ```java
   @PostMapping("/refresh")
   public Response<LoginVo> refreshToken(@RequestBody @Valid RefreshTokenDto dto)
   ```
   - 路径：`/auth/refresh`
   - 不需要JWT认证（应该加入网关白名单）

2. **新增登出接口：**
   ```java
   @PostMapping("/logout")
   public Response<Void> logout(
       @RequestHeader("Authorization") String authHeader,
       @RequestBody @Valid LogoutDto dto)
   ```
   - 路径：`/auth/logout`
   - 从 `Authorization` 请求头提取Access Token
   - 从请求体获取Refresh Token
   - 调用 `authService.logout(accessToken, dto)`

**注意事项：**
- 提取Access Token时，需要去掉 `"Bearer "` 前缀
- 刷新接口应该加入网关白名单（不需要JWT认证）

### 2.7 JwtAuthFilter（网关JWT过滤器）
**文件路径：** `mms-gateway-bc/src/main/java/com/mms/gateway/filter/JwtAuthFilter.java`

**修改内容：**

1. **注入RedisTemplate**
   ```java
   @Resource
   private RedisTemplate<String, Object> redisTemplate;
   ```

2. **在 `filter` 方法中，解析Token后增加以下逻辑：**
   - 提取Token类型：`TokenType tokenType = jwtUtil.extractTokenType(claims);`
   - 验证Token类型必须是 `ACCESS`（如果不是，返回401错误）
   - 提取jti：`String jti = claims.getId();`
   - 检查黑名单：
     ```java
     if (StringUtils.hasText(jti)) {
         String blacklistKey = JwtConstants.TOKEN_BLACKLIST_PREFIX + jti;
         if (Boolean.TRUE.equals(redisTemplate.hasKey(blacklistKey))) {
             return GatewayResponseUtils.writeError(exchange, HttpStatus.UNAUTHORIZED, "登录状态已失效，请重新登录");
         }
     }
     ```

**注意事项：**
- 黑名单检查应该在Token验证通过之后、解析Claims之后进行
- 如果jti为空，跳过黑名单检查（兼容旧Token）

### 2.8 GatewayWhitelistConfig（网关白名单配置）
**文件路径：** `mms-gateway-bc/src/main/java/com/mms/gateway/config/GatewayWhitelistConfig.java`

**修改内容：**
- 确保 `/auth/refresh` 在白名单中（不需要JWT认证）

---

## ⚙️ 三、配置文件修改

### 3.1 application.yml（用户中心服务）
**文件路径：** `mms-usercenter-bc/mms-usercenter-bc-server/src/main/resources/application.yml`

**修改内容：**
```yaml
jwt:
  secret: your-secret-key-here  # 保持不变
  access-expiration: 900000     # Access Token过期时间：15分钟（毫秒）
  refresh-expiration: 604800000 # Refresh Token过期时间：7天（毫秒）
```

### 3.2 application.yml（网关服务）
**文件路径：** `mms-gateway-bc/src/main/resources/application.yml`

**修改内容：**
- 确保Redis配置正确（如果还没有配置）
- 确保网关白名单包含 `/auth/refresh`

---

## 🔍 四、错误码扩展（可选）

### 4.1 ErrorCode枚举
**文件路径：** `mms-common-bc/mms-common-bc-core/src/main/java/com/mms/common/core/enums/ErrorCode.java`

**可选修改：**
- 如果现有的 `INVALID_TOKEN`、`LOGIN_EXPIRED` 不够用，可以新增：
  - `REFRESH_TOKEN_EXPIRED(1006, "刷新令牌已过期")`
  - `TOKEN_TYPE_MISMATCH(1007, "令牌类型不匹配")`

---

## ✅ 五、测试要点

### 5.1 登录接口测试
- [ ] 登录成功，返回 `accessToken` 和 `refreshToken`
- [ ] 验证 `accessToken` 的有效期为15分钟
- [ ] 验证 `refreshToken` 的有效期为7天
- [ ] 验证Token中包含 `jti`、`tokenType`、`username` 等字段

### 5.2 刷新Token接口测试
- [ ] 使用有效的 `refreshToken` 刷新，返回新的双Token
- [ ] 旧的 `refreshToken` 被加入黑名单，无法再次使用
- [ ] 使用过期的 `refreshToken` 刷新，返回错误
- [ ] 使用 `accessToken` 作为 `refreshToken` 刷新，返回错误（类型不匹配）
- [ ] 使用已加入黑名单的 `refreshToken` 刷新，返回错误

### 5.3 登出接口测试
- [ ] 登出成功，`accessToken` 和 `refreshToken` 都被加入黑名单
- [ ] 登出后，使用旧的 `accessToken` 访问接口，返回401（黑名单拦截）
- [ ] 登出后，使用旧的 `refreshToken` 刷新，返回错误

### 5.4 网关过滤器测试
- [ ] 使用有效的 `accessToken` 访问接口，正常通过
- [ ] 使用 `refreshToken` 作为 `accessToken` 访问接口，返回401（类型不匹配）
- [ ] 使用已加入黑名单的 `accessToken` 访问接口，返回401
- [ ] 使用过期的 `accessToken` 访问接口，返回401
- [ ] 刷新接口 `/auth/refresh` 不需要JWT认证（白名单）

### 5.5 Redis黑名单测试
- [ ] 登出后，检查Redis中是否存在黑名单key：`mms:auth:blacklist:{jti}`
- [ ] 验证黑名单key的TTL等于Token的剩余有效时间
- [ ] Token过期后，Redis中的黑名单key自动过期删除

---

## 📝 六、实现顺序建议

1. **第一步：基础结构**
   - 创建 `TokenType` 枚举
   - 创建 `JwtConstants` 常量类
   - 修改 `JwtProperties` 添加新配置字段

2. **第二步：JWT工具类扩展**
   - 修改 `JwtUtil`，添加双Token生成方法
   - 添加Token类型提取方法

3. **第三步：DTO/VO修改**
   - 创建 `RefreshTokenDto`、`LogoutDto`
   - 修改 `LoginVo` 支持双Token

4. **第四步：服务层实现**
   - 修改 `AuthService` 接口
   - 修改 `AuthServiceImpl`，实现双Token登录
   - 实现刷新Token和登出功能
   - 实现Redis黑名单相关方法

5. **第五步：控制器层**
   - 修改 `AuthController`，添加刷新和登出接口

6. **第六步：网关层**
   - 修改 `JwtAuthFilter`，添加黑名单检查
   - 添加Token类型验证

7. **第七步：配置和测试**
   - 修改配置文件
   - 添加白名单配置
   - 进行完整测试

---

## 🎯 七、关键实现细节

### 7.1 Token生成时的jti
```java
String jti = UUID.randomUUID().toString();
Jwts.builder()
    .id(jti)  // 设置jti
    .claims(claims)
    // ... 其他配置
```

### 7.2 黑名单TTL计算
```java
Date expiration = claims.getExpiration();
long ttl = expiration.getTime() - System.currentTimeMillis();
if (ttl > 0) {
    redisTemplate.opsForValue().set(key, value, ttl, TimeUnit.MILLISECONDS);
}
```

### 7.3 Token类型验证
```java
TokenType tokenType = jwtUtil.extractTokenType(claims);
if (tokenType != TokenType.ACCESS) {
    throw new BusinessException(ErrorCode.INVALID_TOKEN, "令牌类型错误");
}
```

### 7.4 从Authorization头提取Token
```java
String authHeader = request.getHeaders().getFirst("Authorization");
if (authHeader != null && authHeader.startsWith("Bearer ")) {
    String token = authHeader.substring(7).trim();
}
```

---

## ⚠️ 八、注意事项

1. **向后兼容性**
   - 如果前端暂时无法修改，可以考虑保留旧的 `token` 字段
   - 或者提供一个过渡期，同时支持新旧两种格式

2. **Redis连接**
   - 确保网关服务和用户中心服务都能访问同一个Redis实例
   - 或者使用Redis集群/哨兵模式

3. **Token刷新策略**
   - 当前实现是"刷新时生成新Token，旧Token加入黑名单"
   - 也可以考虑"刷新时只生成新Access Token，Refresh Token不变"的策略

4. **安全性**
   - Refresh Token应该存储在安全的地方（HttpOnly Cookie或安全的本地存储）
   - Access Token可以存储在内存中

5. **性能考虑**
   - 每次请求都要查询Redis，可能影响性能
   - 可以考虑使用本地缓存（如Caffeine）缓存黑名单查询结果

---

## 📚 九、参考资源

- JWT官方文档：https://jwt.io/
- Spring Data Redis文档：https://spring.io/projects/spring-data-redis
- JJWT库文档：https://github.com/jwtk/jjwt

---

**祝你改造顺利！如有问题，可以参考这个清单逐步实现。** 🚀

