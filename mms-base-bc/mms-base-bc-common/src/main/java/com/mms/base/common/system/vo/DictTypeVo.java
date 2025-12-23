package com.mms.base.common.system.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 实现功能【数据字典类型信息响应 VO】
 * <p>
 * 用于返回数据字典类型信息的响应对象
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "数据字典类型信息响应对象")
public class DictTypeVo {

    @Schema(description = "字典类型ID", example = "1")
    private Long id;

    @Schema(description = "字典类型编码（唯一标识）", example = "user_status")
    private String dictTypeCode;

    @Schema(description = "字典类型名称", example = "用户状态")
    private String dictTypeName;

    @Schema(description = "状态：0-禁用，1-启用", example = "1")
    private Integer status;

    @Schema(description = "排序号", example = "0")
    private Integer sortOrder;

    @Schema(description = "备注", example = "用户账号状态")
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

