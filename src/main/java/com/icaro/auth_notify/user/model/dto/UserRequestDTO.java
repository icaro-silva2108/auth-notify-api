package com.icaro.auth_notify.user.model.dto;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRequestDTO(

        @NotBlank(message = "user name in request should not be blank")
        String name,

        @NotBlank(message = "user email in request should not be blank")
        @Email(message = "user email should have the correct format(ex: user@gmail.com)")
        String email,

        @NotBlank(message = "user password in request should not be blank")
        @Size(min = 8, max = 20, message = "user password in request should have min 8 and max 20 characters")
        String password,

        @NotNull(message = "user birth date in request should not be null")
        @Past(message = "user birth date in request must be in the past")
        LocalDate birthDate
) {}