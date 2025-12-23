package com.mms.usercenter.service.security;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.auth.entity.UserEntity;
import com.mms.usercenter.common.security.constants.UserCenterCacheConstants;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import com.mms.usercenter.service.auth.mapper.UserMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 实现功能【用户详情服务实现类】
 * <p>
 * 实现 Spring Security 的 UserDetailsService 接口
 * 根据用户名从数据库加载用户信息
 * 加载用户角色和权限
 * 返回 SecurityUser 对象
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 11:47:37
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userMapper.selectByUsername(username);
        if (user == null) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND, "用户不存在");
        }

        SecurityUser securityUser = new SecurityUser();
        securityUser.setUserId(user.getId());
        securityUser.setUsername(user.getUsername());
        securityUser.setPassword(user.getPassword());
        securityUser.setRealName(user.getRealName());
        securityUser.setStatus(user.getStatus());
        securityUser.setLocked(user.getLocked());
        securityUser.setLastLoginIp(user.getLastLoginIp());
        securityUser.setLastLoginTime(user.getLastLoginTime());

        // 角色、权限查询（缓存）
        securityUser.setRoles(loadUserRoles(user.getId()));
        securityUser.setPermissions(loadUserPermissions(user.getId()));

        return securityUser;
    }

    /**
     * 加载用户角色编码集合
     * <p>
     * 优先从 Redis 缓存中获取，缓存未命中时查询数据库并写入缓存
     */
    private Set<String> loadUserRoles(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String cacheKey = UserCenterCacheConstants.UserAuthority.USER_ROLE_PREFIX + userId;

        // 从缓存中读取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        Set<String> cachedRoles = convertToStringSet(cached);
        if (!CollectionUtils.isEmpty(cachedRoles)) {
            return cachedRoles;
        }

        // 缓存未命中，查询数据库
        List<String> roleCodeList = roleMapper.selectRoleCodesByUserId(userId);

        // 如果角色列表为空，也缓存一个空集合，避免缓存穿透
        if (CollectionUtils.isEmpty(roleCodeList)) {
            cacheEmptySet(cacheKey);
            return Collections.emptySet();
        }

        // 将 List 转换成 Set
        Set<String> roleCodes = roleCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        // 写入缓存
        cacheSet(roleCodes, cacheKey);
        return roleCodes;
    }

    /**
     * 加载用户权限编码集合
     * <p>
     * 优先从 Redis 缓存中获取，缓存未命中时查询数据库并写入缓存
     */
    private Set<String> loadUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String cacheKey = UserCenterCacheConstants.UserAuthority.USER_PERMISSION_PREFIX + userId;

        // 从缓存中读取
        Object cached = redisTemplate.opsForValue().get(cacheKey);
        Set<String> cachedPermissions = convertToStringSet(cached);
        if (!CollectionUtils.isEmpty(cachedPermissions)) {
            return cachedPermissions;
        }

        // 缓存未命中，查询数据库
        List<String> permissionCodeList = permissionMapper.selectPermissionCodesByUserId(userId);

        // 如果权限列表为空，也缓存一个空集合，避免缓存穿透
        if (CollectionUtils.isEmpty(permissionCodeList)) {
            cacheEmptySet(cacheKey);
            return Collections.emptySet();
        }

        // 将 List 转换成 Set
        Set<String> permissionCodes = permissionCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());

        // 写入缓存
        cacheSet(permissionCodes, cacheKey);
        return permissionCodes;
    }

    /**
     * 将 Redis 中读取到的对象转换为字符串集合
     * 兼容 Set、List 等集合类型
     */
    private Set<String> convertToStringSet(Object cached) {
        if (cached == null) {
            return Collections.emptySet();
        }
        if (cached instanceof Set<?> set) {
            return set.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        if (cached instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(Object::toString)
                    .collect(Collectors.toSet());
        }
        // 其他类型直接转为单元素集合
        return Collections.singleton(cached.toString());
    }

    /**
     * 缓存一个字符串集合
     */
    private void cacheSet(Set<String> values, String... keys) {
        if (values == null) {
            values = Collections.emptySet();
        }
        for (String key : keys) {
            redisTemplate.opsForValue().set(
                    key,
                    values,
                    UserCenterCacheConstants.UserAuthority.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        }
    }

    /**
     * 缓存空集合，避免缓存穿透
     */
    private void cacheEmptySet(String... keys) {
        for (String key : keys) {
            redisTemplate.opsForValue().set(
                    key,
                    new HashSet<>(),
                    UserCenterCacheConstants.UserAuthority.ROLE_PERMISSION_CACHE_TTL_MINUTES,
                    TimeUnit.MINUTES
            );
        }
    }
}