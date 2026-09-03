package com.icaro.auth_notify.messaging.publisher;

import com.icaro.auth_notify.messaging.dto.UserEventDTO;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreated(UserEventDTO event) {

        rabbitTemplate.convertAndSend(
                "user-events-exchange",
                "user.created",
                event
        );
    }

    public void publishUserUpdated(UserEventDTO event) {

        rabbitTemplate.convertAndSend(
                "user-events-exchange",
                "user.updated",
                event
        );
    }
}