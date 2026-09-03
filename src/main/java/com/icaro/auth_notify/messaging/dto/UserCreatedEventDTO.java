package com.icaro.auth_notify.messaging.dto;

public record UserCreatedEventDTO(

        String name,
        String userEmail
) {}