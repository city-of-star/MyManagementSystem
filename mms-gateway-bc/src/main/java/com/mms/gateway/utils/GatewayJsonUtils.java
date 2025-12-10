package com.mms.gateway.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;

import java.nio.charset.StandardCharsets;

/**
 * 实现功能【网关 JSON 工具类】
 * <p>
 * 统一处理 JSON 序列化逻辑，避免重复代码
 * </p>
 *
 * @author li.hongyu
 * @date 2025-12-01 15:36:57
 */
public class GatewayJsonUtils {

    /**
     * JSON 序列化工具（线程安全）
     */
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    /**
     * 将对象序列化为 JSON 字节数组
     *
     * @param obj 待序列化的对象
     * @return JSON 字节数组
     */
    public static byte[] toJsonBytes(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj).getBytes(StandardCharsets.UTF_8);
        } catch (JsonProcessingException e) {
            // 序列化失败时的兜底响应
            String fallback = "{\"code\":" + HttpStatus.INTERNAL_SERVER_ERROR.value() +
                    ",\"message\":\"响应序列化失败\",\"data\":null}";
            return fallback.getBytes(StandardCharsets.UTF_8);
        }
    }

    /**
     * 将对象序列化为 JSON 字符串
     *
     * @param obj 待序列化的对象
     * @return JSON 字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return OBJECT_MAPPER.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            return "{\"code\":" + HttpStatus.INTERNAL_SERVER_ERROR.value() +
                    ",\"message\":\"响应序列化失败\",\"data\":null}";
        }
    }

    /**
     * 私有构造函数，防止实例化
     */
    private GatewayJsonUtils() {
        throw new UnsupportedOperationException("工具类不允许实例化");
    }
}

