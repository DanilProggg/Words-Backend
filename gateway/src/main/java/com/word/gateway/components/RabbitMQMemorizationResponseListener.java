package com.word.gateway.components;


import com.word.gateway.configs.RabbitMQMemorizationConfiguration;
import com.word.gateway.controllers.MemorizationController;
import com.word.gateway.dtos.RabbitMQResponse;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class RabbitMQMemorizationResponseListener {

    private final MemorizationController memorizationController;

    public RabbitMQMemorizationResponseListener(MemorizationController memorizationController) {
        this.memorizationController = memorizationController;
    }

    @RabbitListener(queues = RabbitMQMemorizationConfiguration.RESPONSE_QUEUE)
    public void processResponse(RabbitMQResponse response,
                                @Header(AmqpHeaders.CORRELATION_ID) String correlationId) {

        // Передаем ответ в контроллер
        memorizationController.completeResponse(correlationId, response);
    }
}
