package com.mms.base.common.system.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

/**
 * 实现功能【批量删除数据字典数据请求 DTO】
 * <p>
 * 用于批量删除数据字典数据的请求参数
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
@Data
@Schema(description = "批量删除数据字典数据请求参数")
public class DictDataBatchDeleteDto {

    @NotEmpty(message = "字典数据ID列表不能为空")
    @Schema(description = "字典数据ID列表", requiredMode = Schema.RequiredMode.REQUIRED, example = "[1, 2, 3]")
    private List<Long> dictDataIds;
}

