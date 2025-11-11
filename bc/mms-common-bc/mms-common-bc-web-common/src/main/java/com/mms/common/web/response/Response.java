package com.mms.common.web.response;

import lombok.Data;
import org.slf4j.MDC;

/**
 * 实现功能【返回体】
 *
 * @author li.hongyu
 * @date 2025-10-28 15:51:29
 */
@Data
public class Response<T> {

    // 成功消息
    public static final String SUCCESS_MESSAGE = "success";
    // 成功状态码
    public static final int SUCCESS_CODE = 200;
    // 业务失败基础码
    public static final int BUSINESS_ERROR_BASE = 1000;

    private Integer code;
    private String message;
    private T data;
    private String traceId = MDC.get("traceId");

    // 私有构造函数
    private Response(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    // 成功响应
    public static <T> Response<T> success() {
        return new Response<>(SUCCESS_CODE, SUCCESS_MESSAGE, null);
    }

    // 成功响应（带数据）
    public static <T> Response<T> success(T data) {
        return new Response<>(SUCCESS_CODE, SUCCESS_MESSAGE, data);
    }

    // 业务失败
    public static <T> Response<T> fail(Integer code, String message) {
        return new Response<>(BUSINESS_ERROR_BASE + code, message, null);
    }

    // 系统级错误
    public static <T> Response<T> error(Integer code, String message) {
        return new Response<>(code, message, null);
    }
}

