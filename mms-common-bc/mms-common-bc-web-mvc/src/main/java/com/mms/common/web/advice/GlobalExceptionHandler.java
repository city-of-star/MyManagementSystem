package com.mms.common.web.advice;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.core.response.Response;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Set;

/**
 * 实现功能【全局异常捕获处理器】
 * <p>
 * 仅适用于Spring MVC服务（业务服务），不适用于Spring Cloud Gateway（WebFlux）
 * <p/>
 *
 * @author li.hongyu
 * @date 2025-10-28 20:22:30
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理业务异常(HTTP 400)
     * 支持两种方式：规范枚举方式和自定义消息方式
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: 【{}】", e.getMessage());
        return Response.fail(e.getCode(), e.getMessage());
    }

    /**
     * 处理服务器异常(HTTP 500)
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException.class)
    public Response<?> handleServerException(ServerException e) {
        log.error("服务器异常: 【{}】", getCurrentRequestUrl(), e);
        return Response.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 处理请求参数约束违反异常(HTTP 400)
     * 处理 @RequestBody @Valid 注解触发的验证异常（ POST方法 ）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Response<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        // 获取第一个验证失败的错误信息
        FieldError fieldError = e.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "参数验证失败";
        
        log.warn("参数约束违反异常: 【{}】", message);
        return Response.error(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    /**
     * 处理请求参数约束违反异常(HTTP 400)
     * 处理 @RequestParam @Valid 注解触发的验证异常（ GET方法 ）
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Response<?> handleConstraintViolationException(ConstraintViolationException e) {
        // 获取第一个验证失败的错误信息
        Set<ConstraintViolation<?>> violations = e.getConstraintViolations();
        String message = violations.stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("参数验证失败");
        
        log.warn("参数约束违反异常: 【{}】", message);
        return Response.error(ErrorCode.PARAM_INVALID.getCode(), message);
    }

    /**
     * 处理请求参数解析异常(HTTP 400)
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求参数解析异常: 【{}】", getCurrentRequestUrl(), e);
        return Response.error(ErrorCode.PARAM_INVALID.getCode(), ErrorCode.PARAM_INVALID.getMessage());
    }

    /**
     * 处理请求方法不匹配异常(HTTP 405)
     * 开发环境提供更友好的提示
     */
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        String method = e.getMethod();
        String[] supportedMethods = e.getSupportedMethods();
        String supportedMethodsStr = supportedMethods != null ? String.join(", ", supportedMethods) : "未知";
        String message = String.format("请求方法 %s 不被支持，支持的方法: %s", method, supportedMethodsStr);

        log.warn("请求方法不匹配异常: 【请求方法: {}，支持的方法: {}】", method, supportedMethodsStr);
        return Response.error(ErrorCode.METHOD_NOT_ALLOWED.getCode(), message);
    }

    /**
     * 处理资源不存在异常(HTTP 404)
     * 注意：需要配置 spring.mvc.throw-exception-if-no-handler-found=true 才能生效
     */
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoResourceFoundException.class)
    public Response<?> handleNoResourceFoundException(NoResourceFoundException e) {
        String method = e.getHttpMethod().name();
        String path = e.getResourcePath();
        String message = String.format("接口不存在: %s %s", method, path);
        
        log.warn("接口不存在: 【{} {}】", method, path);
        return Response.error(ErrorCode.NOT_FOUND.getCode(), message);
    }

    /**
     * 兜底异常处理，捕获所有未被其他处理器处理的异常(HTTP 500)
     */
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<?> handleAllUncaughtException(Exception e) {
        log.error("未知异常: 【{}】", e.getMessage(), e);
        return Response.error(ErrorCode.SYSTEM_ERROR.getCode(), ErrorCode.SYSTEM_ERROR.getMessage());
    }

    /**
     * 获取当前请求的完整 URL
     * @return 格式化的请求URL字符串
     */
    private String getCurrentRequestUrl() {
        try {
            // 从RequestContextHolder中获取当前请求
            ServletRequestAttributes attributes = (ServletRequestAttributes)
                    RequestContextHolder.getRequestAttributes();

            if (attributes == null) {
                return "未知接口";
            }

            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();
            String path = request.getRequestURI();
            String queryString = request.getQueryString();

            StringBuilder urlBuilder = new StringBuilder();
            urlBuilder.append(method).append(" ").append(path);

            if (StringUtils.isNotBlank(queryString)) {
                urlBuilder.append("?").append(queryString);
            }

            return urlBuilder.toString();
        } catch (Exception e) {
            // 捕获异常避免影响主流程
            log.warn("获取当前请求URL失败", e);
            return "未知接口";
        }
    }
}

