package com.springcloud.webfluxdemo.springMVC.exception;

import lombok.Data;

@Data
public class CheckException extends RuntimeException {

    /**
     * 出错字段的名字
     */
    private String fiendName;

    /**
     * 出错字段的值
     */
    private String fieldValue;

    public CheckException() {
    }

    public CheckException(String message) {
        super(message);
    }

    public CheckException(String message, Throwable cause) {
        super(message, cause);
    }

    public CheckException(Throwable cause) {
        super(cause);
    }

    public CheckException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public CheckException(String fiendName, String fieldValue) {
        this.fiendName = fiendName;
        this.fieldValue = fieldValue;
    }
}
