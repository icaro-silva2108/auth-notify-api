package com.icaro.auth_notify.messaging.publisher;

import com.icaro.auth_notify.messaging.dto.UserEventMessageDTO;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publishUserCreated(UserEventMessageDTO event) {

        rabbitTemplate.convertAndSend(
                "user-events-exchange",
                "user.created",
                event
        );
    }

    public void publishUserUpdated(UserEventMessageDTO event) {

        rabbitTemplate.convertAndSend(
                "user-events-exchange",
                "user.updated",
                event
        );
    }
}