package com.mvo.individualsapi.exception;

import lombok.Getter;

public class ApiException extends RuntimeException {

    @Getter
    protected String errorCode;

    public ApiException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
