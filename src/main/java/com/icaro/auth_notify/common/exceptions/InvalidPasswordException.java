package com.icaro.auth_notify.common.exceptions;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException() {
        super("user password should have min 8 max 20 characters");
    }
}