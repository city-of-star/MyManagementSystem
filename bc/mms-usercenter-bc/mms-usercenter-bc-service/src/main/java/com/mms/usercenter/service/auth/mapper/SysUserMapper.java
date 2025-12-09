package com.mms.usercenter.service.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mms.usercenter.common.auth.entity.SysUserEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * 实现功能【用户实体 Mapper】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 17:30:28
 */
@Mapper
public interface SysUserMapper extends BaseMapper<SysUserEntity> {

    /**
     * 根据用户名查询用户（用于登录）
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUserEntity selectByUsername(@Param("username") String username);
}

