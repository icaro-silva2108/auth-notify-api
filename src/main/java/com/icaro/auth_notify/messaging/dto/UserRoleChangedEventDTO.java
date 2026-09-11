package com.icaro.auth_notify.messaging.dto;

import com.icaro.auth_notify.user.model.enums.UserRole;

public record UserRoleChangedEventDTO(

        String name,
        String userEmail,
        UserRole role
) {}