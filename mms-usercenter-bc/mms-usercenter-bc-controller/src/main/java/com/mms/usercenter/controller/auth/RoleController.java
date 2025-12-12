package com.mms.usercenter.controller.auth;

import com.mms.usercenter.service.auth.service.RoleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 实现功能【角色服务 Controller】
 * <p>
 *
 * <p>
 *
 * @author li.hongyu
 * @date 2025-12-12 09:36:59
 */
@Tag(name = "角色服务", description = "角色服务相关接口")
@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;


}