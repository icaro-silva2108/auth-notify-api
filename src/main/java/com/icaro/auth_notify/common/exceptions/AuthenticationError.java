package com.icaro.auth_notify.common.exceptions;

public class AuthenticationError extends RuntimeException {
    public AuthenticationError() {
        super("failed to authenticate user");
    }
}