package com.icaro.auth_notify.common.exceptions.globalExceptionHandler.model;

import java.time.LocalDateTime;

public record ErrorMessageDTO(

        LocalDateTime time,
        int status,
        String message
) {}