package com.mms.usercenter.service.security;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.usercenter.common.auth.entity.SysUserEntity;
import com.mms.usercenter.common.security.entity.SecurityUser;
import com.mms.usercenter.service.auth.mapper.SysUserMapper;
import jakarta.annotation.Resource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    private SysUserMapper sysUserMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUserEntity user = sysUserMapper.selectByUsername(username);
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

        // 预留角色、权限查询（后续可由角色/权限服务或缓存提供）
        securityUser.setRoles(loadUserRoles(user.getId()));
        securityUser.setPermissions(loadUserPermissions(user.getId()));

        return securityUser;
    }

    /**
     * 后续可改为远程调用/缓存，当前先返回空集占位
     */
    private Set<String> loadUserRoles(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        // TODO: 接入角色服务或本地 Mapper 查询角色编码
        return new HashSet<>();
    }

    /**
     * 后续可改为远程调用/缓存，当前先返回空集占位
     */
    private Set<String> loadUserPermissions(Long userId) {
        if (userId == null) {
            return Collections.emptySet();
        }
        // TODO: 接入权限服务或本地 Mapper 查询权限编码
        return new HashSet<>();
    }
}