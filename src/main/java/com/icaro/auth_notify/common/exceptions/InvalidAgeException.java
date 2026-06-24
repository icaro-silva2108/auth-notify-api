package com.icaro.auth_notify.common.exceptions;

public class InvalidAgeException extends RuntimeException {
    public InvalidAgeException() {
        super("user must be at least 14 years old");
    }
}