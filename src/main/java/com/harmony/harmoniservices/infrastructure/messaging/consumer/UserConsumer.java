package com.harmony.harmoniservices.infrastructure.messaging.consumer;


import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.harmony.harmoniservices.core.domain.events.UserEvent;
import com.harmony.harmoniservices.core.ports.cases.UserService;
import com.harmony.harmoniservices.core.ports.incoming.UserEventConsumer;

@Component
public class UserConsumer implements UserEventConsumer {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    public static final String USER_QUEUE_NAME = "UserQueue";


    public UserConsumer(UserService userService, ObjectMapper objectMapper) {
        this.userService = userService;
        this.objectMapper = objectMapper;
    }

    @Override
    @RabbitListener(queues = USER_QUEUE_NAME)
    public void handleUserEvent(UserEvent event) {
       
    }
}