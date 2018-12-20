package com.bigpanda.commons.web.errors;

/**
 * Created by erik on 11/12/17.
 */
public enum GeneralErrorCode implements ErrorCode {
    GENERAL_ERROR(1),
    RESOURCE_NOT_FOUND(2),
    INVALID_INPUT(3),
    UNAUTHORIZED(4),
    DECODE_ERROR(5);

    private final int code;

    GeneralErrorCode(int code) {
        this.code = code;
    }

    @Override
    public int getCode() {
        return code;
    }
}
