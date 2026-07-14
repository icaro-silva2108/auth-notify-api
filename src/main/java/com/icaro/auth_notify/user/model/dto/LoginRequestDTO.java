package com.icaro.auth_notify.user.model.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequestDTO(

        @NotBlank(message = "user email in request should not be blank")
        @Email(message = "user email should have the correct format(ex: user@gmail.com)")
        String email,

        @NotBlank(message = "user password in request should not be blank")
        @Size(min = 8, max = 20, message = "user password in request should have min 8 and max 20 characters")
        String password
) {}