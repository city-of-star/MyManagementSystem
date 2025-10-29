package com.mms.common.web.advice;

import com.mms.common.core.enums.ErrorCode;
import com.mms.common.core.exceptions.BusinessException;
import com.mms.common.core.exceptions.ServerException;
import com.mms.common.web.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

/**
 * 实现功能【全局异常捕获处理类】
 *
 * @author li.hongyu
 * @date 2025-10-28 20:22:30
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 处理业务异常(HTTP 400)(自定义异常)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BusinessException.class)
    public Response<?> handleBusinessException(BusinessException e) {
        if (e.getMessage() == null) {
            // 处理业务异常消息
            log.warn("业务异常: 【{}】", e.getErrorCode().getMessage());
            return Response.fail(e.getErrorCode().getCode(), e.getErrorCode().getMessage());
        } else {
            // 处理自定义业务异常消息
            log.warn("业务异常: 【{}】", e.getMessage());
            return Response.fail(ErrorCode.INVALID_OPERATION.getCode(), e.getMessage());
        }
    }

    // 处理服务器异常(HTTP 500)(自定义异常)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(ServerException.class)
    public Response<?> handleServerException(ServerException e) {
        log.error("服务器异常: 【{}】", e.getMessage());
        return Response.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.SYSTEM_ERROR.getMessage());
    }

    // 请求参数解析异常(HTTP 400)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public Response<?> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        log.warn("请求参数解析异常: 【{}】", e.getMessage());
        return Response.error(HttpStatus.BAD_REQUEST.value(), ErrorCode.PARAMETER_PARSING_EXCEPTION.getMessage());
    }

    // 接口不存在异常(HTTP 404)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NoHandlerFoundException.class)
    public Response<?> handleNotFound(NoHandlerFoundException e) {
        log.warn("接口不存在异常: 【{}】", e.getRequestURL());
        return Response.error(HttpStatus.NOT_FOUND.value(), ErrorCode.RESOURCE_NOT_EXIST.getMessage());
    }

    // 请求方法不匹配异常(HTTP 405)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public Response<?> handleMethodNotAllowed(HttpRequestMethodNotSupportedException e) {
        log.warn("请求方法不匹配异常: 【{}】", e.getMethod());
        return Response.error(HttpStatus.METHOD_NOT_ALLOWED.value(), ErrorCode.METHOD_MISMATCH.getMessage());
    }

    // 兜底异常处理，捕获所有未被其他处理器处理的异常(HTTP 500)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Response<?> handleAllUncaughtException(Exception e) {
        log.error("未知异常: 【{}】", e.getMessage(), e);  // 记录完整的异常堆栈信息
        return Response.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ErrorCode.SYSTEM_ERROR.getMessage());
    }
}