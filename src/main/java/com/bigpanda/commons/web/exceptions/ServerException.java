package com.bigpanda.commons.web.exceptions;

import com.bigpanda.commons.web.errors.ErrorCode;

/**
 * Created by erik on 10/25/17.
 */
public class ServerException extends RuntimeException {

    private final ErrorCode errorCode;

    public ServerException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public ServerException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ServerException(String message) {
        super(message);
        errorCode = null;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}
