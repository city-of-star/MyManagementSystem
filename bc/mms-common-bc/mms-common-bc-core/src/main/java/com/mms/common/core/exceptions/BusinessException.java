package com.mms.common.core.exceptions;

import com.mms.common.core.enums.ErrorCode;
import lombok.Getter;
import java.io.Serial;

/**
 * 实现功能【自定义业务异常】
 *
 * @author li.hongyu
 * @date 2025-10-28 20:21:27
 */
@Getter
public class BusinessException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 9196224339724626039L;

    private final ErrorCode errorCode;
    private String message = null;

    public BusinessException(ErrorCode errorCode, String message) {
        this.errorCode = errorCode;
        this.message = message;
    }

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }
}
