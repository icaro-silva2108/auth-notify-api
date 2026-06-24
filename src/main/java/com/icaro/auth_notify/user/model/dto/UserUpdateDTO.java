package com.icaro.auth_notify.user.model.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserUpdateDTO(

        String name,

        @Email(message = "user email should have the correct format(ex: user@gmail.com)")
        String email,

        @Size(min = 8, max = 20, message = "user password in request should have min 8 and max 20 characters")
        String password,

        @Past
        LocalDate birthDate
) {}