package com.icaro.auth_notify.common.exceptions;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException() {
        super("this email is already in use");
    }
}