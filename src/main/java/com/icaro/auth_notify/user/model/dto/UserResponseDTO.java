package com.icaro.auth_notify.user.model.dto;

import com.icaro.auth_notify.user.model.enums.UserRole;

import java.time.LocalDate;

public record UserResponseDTO(

        Long id,
        String name,
        String email,
        LocalDate birthDate,
        UserRole role,
        boolean active
) {}