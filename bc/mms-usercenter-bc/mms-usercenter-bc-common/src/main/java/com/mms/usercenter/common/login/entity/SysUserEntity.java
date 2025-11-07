package com.mms.usercenter.common.login.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 实现功能【用户实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-11-07 09:41:03
 */
@Data
@TableName("sys_user")
@Schema(description = "系统用户实体")
public class SysUserEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String username;

    private String password;

    private String nickname;

    private String realName;

    private String avatar;

    private String email;

    private String phone;

    private Integer gender;

    private LocalDate birthday;

    private Integer status;

    private Integer locked;

    private LocalDateTime lockTime;

    private String lockReason;

    private LocalDateTime lastLoginTime;

    private String lastLoginIp;

    private LocalDateTime passwordUpdateTime;

    private String remark;

    private Integer deleted;

    private Long createBy;

    private LocalDateTime createTime;

    private Long updateBy;

    private LocalDateTime updateTime;
}