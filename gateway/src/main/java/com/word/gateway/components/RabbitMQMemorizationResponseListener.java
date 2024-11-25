package com.word.gateway.components;


import com.word.gateway.controllers.MemorizationController;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class RabbitMQMemorizationResponseListener {

    private final MemorizationController memorizationController;

    private final String memorizationResponseQueue = "memorization_response_queue";

    public RabbitMQMemorizationResponseListener(MemorizationController memorizationController) {
        this.memorizationController = memorizationController;
    }

    @RabbitListener(queues = memorizationResponseQueue)
    public void processResponse(Message message) {
        String correlationId = message.getMessageProperties().getCorrelationId();
        String body = new String(message.getBody(), StandardCharsets.UTF_8);

        // Передаем ответ в контроллер
        memorizationController.completeResponse(correlationId, body);
    }
}
