package com.mms.usercenter.common.auth.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实现功能【角色权限关联实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 14:42:27
 */
@Data
@TableName("sys_role_permission")
@Schema(description = "角色权限关联实体")
public class SysRolePermissionEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "关联ID")
    private Long id;

    @Schema(description = "角色ID")
    private Long roleId;

    @Schema(description = "权限ID")
    private Long permissionId;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}