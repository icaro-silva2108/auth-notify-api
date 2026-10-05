package com.icaro.auth_notify.common.exceptions.globalExceptionHandler.model;

import java.time.LocalDateTime;
import java.util.List;

public record InvalidArgumentsMessageDTO(

        LocalDateTime time,
        int status,
        String message,
        List<InvalidFieldDTO> fields
) {}