package com.campus.trade.common;

/**
 * 业务异常：Service 层抛出，Servlet 捕获后转成用户可读的提示。
 */
public class BizException extends RuntimeException {
    public BizException(String message) {
        super(message);
    }

    public BizException(String message, Throwable cause) {
        super(message, cause);
    }
}
