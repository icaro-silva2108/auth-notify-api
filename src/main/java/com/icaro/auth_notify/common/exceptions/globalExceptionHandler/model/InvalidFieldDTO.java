package com.icaro.auth_notify.common.exceptions.globalExceptionHandler.model;

public record InvalidFieldDTO(

        String field,
        String error
) {}