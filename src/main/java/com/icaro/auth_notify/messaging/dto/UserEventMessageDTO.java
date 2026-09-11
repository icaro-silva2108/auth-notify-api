package com.icaro.auth_notify.messaging.dto;

import com.icaro.auth_notify.messaging.enums.UserEventType;
import tools.jackson.databind.JsonNode;

public record UserEventMessageDTO(

        UserEventType type,
        JsonNode payload
) {}