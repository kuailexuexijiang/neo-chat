package com.neo.types.exception;


public class NeoChatException extends RuntimeException {

    /**
     * 异常码
     */
    private String code;

    /**
     * 异常信息
     */
    private String message;

    public NeoChatException(String code) {
        this.code = code;
    }

    public NeoChatException(String code, Throwable cause) {
        this.code = code;
        super.initCause(cause);
    }

    public NeoChatException(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public NeoChatException(String code, String message, Throwable cause) {
        this.code = code;
        this.message = message;
        super.initCause(cause);
    }

}
