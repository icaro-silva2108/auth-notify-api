package com.icaro.auth_notify.messaging.publisher;

import com.icaro.auth_notify.messaging.dto.UserCreatedEventDTO;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreated(UserCreatedEventDTO event) {

        rabbitTemplate.convertAndSend(
                "user-events-exchange",
                "user.created",
                event
        );
    }
}