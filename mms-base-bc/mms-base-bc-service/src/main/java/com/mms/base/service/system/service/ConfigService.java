package com.mms.base.service.system.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.system.dto.*;
import com.mms.base.common.system.vo.ConfigVo;

/**
 * 实现功能【系统配置服务】
 * <p>
 * 提供系统配置管理的核心业务方法
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-23 11:21:50
 */
public interface ConfigService {

    /**
     * 分页查询系统配置列表
     *
     * @param dto 查询条件
     * @return 分页配置列表
     */
    Page<ConfigVo> getConfigPage(ConfigPageQueryDto dto);

    /**
     * 根据配置ID查询配置详情
     *
     * @param configId 配置ID
     * @return 配置信息
     */
    ConfigVo getConfigById(Long configId);

    /**
     * 根据配置键查询配置信息
     *
     * @param configKey 配置键
     * @return 配置信息
     */
    ConfigVo getConfigByKey(String configKey);

    /**
     * 创建系统配置
     *
     * @param dto 配置创建参数
     * @return 创建的配置信息
     */
    ConfigVo createConfig(ConfigCreateDto dto);

    /**
     * 更新系统配置信息
     *
     * @param dto 配置更新参数
     * @return 更新后的配置信息
     */
    ConfigVo updateConfig(ConfigUpdateDto dto);

    /**
     * 删除系统配置（逻辑删除）
     *
     * @param configId 配置ID
     */
    void deleteConfig(Long configId);

    /**
     * 批量删除系统配置（逻辑删除）
     *
     * @param dto 批量删除参数
     */
    void batchDeleteConfig(ConfigBatchDeleteDto dto);

    /**
     * 切换系统配置状态（启用/禁用）
     *
     * @param dto 状态切换参数
     */
    void switchConfigStatus(ConfigStatusSwitchDto dto);

    /**
     * 检查配置键是否存在
     *
     * @param configKey 配置键
     * @return true-存在，false-不存在
     */
    boolean existsByConfigKey(String configKey);
}
