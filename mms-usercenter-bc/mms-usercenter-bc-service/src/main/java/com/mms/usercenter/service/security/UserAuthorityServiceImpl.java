package com.mms.usercenter.service.security;

import com.mms.common.core.constants.security.UserCenterCacheConstants;
import com.mms.usercenter.common.security.vo.UserAuthorityVo;
import com.mms.usercenter.service.auth.mapper.PermissionMapper;
import com.mms.usercenter.service.auth.mapper.RoleMapper;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
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

@Service
public class UserAuthorityServiceImpl implements UserAuthorityService {

    @Resource
    private RoleMapper roleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    @Transactional(readOnly = true)
    public UserAuthorityVo getUserAuthorities(Long userId) {
        UserAuthorityVo vo = new UserAuthorityVo();
        vo.setUserId(userId);
        vo.setRoles(loadUserRoles(userId));
        vo.setPermissions(loadUserPermissions(userId));
        return vo;
    }

    private Set<String> loadUserRoles(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String cacheKey = UserCenterCacheConstants.UserAuthority.USER_ROLE_PREFIX + userId;
        Set<String> cachedRoles = convertToStringSet(redisTemplate.opsForValue().get(cacheKey));
        if (!CollectionUtils.isEmpty(cachedRoles)) {
            return cachedRoles;
        }

        List<String> roleCodeList = roleMapper.selectRoleCodesByUserId(userId);
        if (CollectionUtils.isEmpty(roleCodeList)) {
            cacheEmptySet(cacheKey);
            return Collections.emptySet();
        }

        Set<String> roleCodes = roleCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        cacheSet(roleCodes, cacheKey);
        return roleCodes;
    }

    private Set<String> loadUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }

        String cacheKey = UserCenterCacheConstants.UserAuthority.USER_PERMISSION_PREFIX + userId;
        Set<String> cachedPermissions = convertToStringSet(redisTemplate.opsForValue().get(cacheKey));
        if (!CollectionUtils.isEmpty(cachedPermissions)) {
            return cachedPermissions;
        }

        List<String> permissionCodeList = permissionMapper.selectPermissionCodesByUserId(userId);
        if (CollectionUtils.isEmpty(permissionCodeList)) {
            cacheEmptySet(cacheKey);
            return Collections.emptySet();
        }

        Set<String> permissionCodes = permissionCodeList.stream()
                .filter(StringUtils::hasText)
                .collect(Collectors.toSet());
        cacheSet(permissionCodes, cacheKey);
        return permissionCodes;
    }

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
        return Collections.singleton(cached.toString());
    }

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

