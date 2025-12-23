package com.mms.base.service.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.DictTypeVo;

import java.util.List;

/**
 * 实现功能【数据字典类型服务】
 * <p>
 * 提供数据字典类型管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
public interface DictTypeService {

    /**
     * 分页查询数据字典类型列表
     *
     * @param dto 查询条件
     * @return 分页字典类型列表
     */
    Page<DictTypeVo> getDictTypePage(DictTypePageQueryDto dto);

    /**
     * 根据字典类型ID查询字典类型详情
     *
     * @param dictTypeId 字典类型ID
     * @return 字典类型信息
     */
    DictTypeVo getDictTypeById(Long dictTypeId);

    /**
     * 根据字典类型编码查询字典类型信息
     *
     * @param dictTypeCode 字典类型编码
     * @return 字典类型信息
     */
    DictTypeVo getDictTypeByCode(String dictTypeCode);

    /**
     * 查询所有启用的字典类型列表
     *
     * @return 字典类型列表
     */
    List<DictTypeVo> listAllEnabledDictTypes();

    /**
     * 创建数据字典类型
     *
     * @param dto 字典类型创建参数
     * @return 创建的字典类型信息
     */
    DictTypeVo createDictType(DictTypeCreateDto dto);

    /**
     * 更新数据字典类型信息
     *
     * @param dto 字典类型更新参数
     * @return 更新后的字典类型信息
     */
    DictTypeVo updateDictType(DictTypeUpdateDto dto);

    /**
     * 删除数据字典类型（逻辑删除）
     *
     * @param dictTypeId 字典类型ID
     */
    void deleteDictType(Long dictTypeId);

    /**
     * 批量删除数据字典类型（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteDictType(DictTypeBatchDeleteDto dto);

    /**
     * 切换数据字典类型状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchDictTypeStatus(DictTypeStatusSwitchDto dto);

    /**
     * 检查字典类型编码是否存在
     *
     * @param dictTypeCode 字典类型编码
     * @return true-存在，false-不存在
     */
    boolean existsByDictTypeCode(String dictTypeCode);
}
