package com.mms.base.controller.test;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mms.base.common.test.dto.TestDTO;
import com.mms.base.common.test.entity.TestEntity;
import com.mms.base.service.test.service.TestService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

/**
 * 实现功能【测试基础功能】
 *
 * @author li.hongyu
 * @date 2025-10-12 19:33:28
 */
@RestController
@RequestMapping("/test")
@Tag(name = "测试管理", description = "测试相关接口")
public class TestController {

    @Resource
    private TestService testService;

    @GetMapping("/1")
    @Operation(summary = "基础测试接口", description = "用于测试系统基础功能是否正常")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "测试成功", 
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public String test1() {
        return testService.test();
    }

    @PostMapping("/getPage")
    @Operation(summary = "分页查询测试列表", description = "根据条件分页查询测试数据列表")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "查询成功", 
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(responseCode = "400", description = "请求参数错误"),
            @ApiResponse(responseCode = "500", description = "服务器内部错误")
    })
    public Page<TestEntity> getPage(
            @Parameter(description = "查询条件", required = true)
            @RequestBody TestDTO dto) {
        return testService.getPage(dto);
    }
}