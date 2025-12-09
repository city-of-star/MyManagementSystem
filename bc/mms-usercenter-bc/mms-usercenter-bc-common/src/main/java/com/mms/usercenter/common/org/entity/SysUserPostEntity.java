package com.mms.usercenter.common.org.entity;

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
 * 实现功能【用户岗位关联实体】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-09 14:42:42
 */
@Data
@TableName("sys_user_post")
@Schema(description = "用户岗位关联实体")
public class SysUserPostEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    @Schema(description = "关联ID")
    private Long id;

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "岗位ID")
    private Long postId;

    @Schema(description = "是否主岗位：0-否，1-是")
    private Integer isPrimary;

    @TableField(value = "create_by", fill = FieldFill.INSERT)
    @Schema(description = "创建人ID")
    private Long createBy;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    @Schema(description = "创建时间")
    private LocalDateTime createTime;
}

