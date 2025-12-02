package com.mms.common.web.context;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 实现功能【用户上下文实体类】
 * <p>
 * 用于存储当前请求的用户信息
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-02 11:14:31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContext {

    /**
     * 用户名
     */
    private String username;

    /**
     * 客户端IP地址
     */
    private String clientIp;
}