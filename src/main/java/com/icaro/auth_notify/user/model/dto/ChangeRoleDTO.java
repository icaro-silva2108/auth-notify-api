package com.icaro.auth_notify.user.model.dto;

import com.icaro.auth_notify.user.model.enums.UserRole;
import jakarta.validation.constraints.NotNull;

public record ChangeRoleDTO(

        @NotNull(message = "user role should not be null to change")
        UserRole role
) {}