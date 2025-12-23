package com.mms.usercenter.service.security;

import com.mms.usercenter.common.security.vo.UserAuthorityVo;

/**
 * 用户角色/权限查询服务
 */
public interface UserAuthorityService {

    /**
     * 根据用户ID查询角色与权限（带缓存）
     */
    UserAuthorityVo getUserAuthorities(Long userId);
}

