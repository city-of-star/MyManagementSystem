package com.mms.base.common.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【系统配置信息响应 VO】
 * <p>
 * 用于返回系统配置信息的响应对象
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "系统配置信息响应对象")
public class ConfigVo {

    @Schema(description = "配置ID", example = "1")
    private Long id;

    @Schema(description = "配置键（唯一标识）", example = "system.name")
    private String configKey;

    @Schema(description = "配置值", example = "MyManagementSystem")
    private String configValue;

    @Schema(description = "配置类型：string-字符串，number-数字，boolean-布尔值，json-JSON对象", example = "string")
    private String configType;

    @Schema(description = "配置名称/描述", example = "系统名称")
    private String configName;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "是否可编辑：0-否（系统配置），1-是（用户配置）", example = "1")
    private Integer editable;

    @Schema(description = "备注", example = "系统名称配置")
    private String remark;

    @Schema(description = "创建人ID", example = "1")
    private Long createBy;

    @Schema(description = "创建时间", example = "2025-12-23 11:21:50")
    private LocalDateTime createTime;

    @Schema(description = "更新人ID", example = "1")
    private Long updateBy;

    @Schema(description = "更新时间", example = "2025-12-23 11:21:50")
    private LocalDateTime updateTime;
}

