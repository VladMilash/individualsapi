package com.mvo.individualsapi.exception;

public class PasswordsMatchException extends ApiException {

    public PasswordsMatchException(String message, String errorCode) {
        super(message, errorCode);
    }
}
