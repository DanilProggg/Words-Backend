package com.word.memorization.configs;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.handler.annotation.Header;

@Configuration
public class RabbitMQConfiguration {
    private final String memorizationRequestQueue = "memorization_request_queue";
    private final String memorizationResponseQueue = "memorization_response_queue";
    private final String memorizationExchange = "memorization_exchange";

    @Bean
    public Queue memorizationRequestQueue(){
        return new Queue(memorizationRequestQueue, true);
    }
    @Bean
    public Queue memorizationResponseQueue(){
        return new Queue(memorizationResponseQueue, true);
    }


    @Bean
    Exchange Exchange() {
        return new TopicExchange(memorizationExchange, true, false);
    }

    @Bean
    Binding requestBinding(Queue memorizationRequestQueue, Exchange memorizationExchange){
        return BindingBuilder.bind(memorizationRequestQueue).to(memorizationExchange).with("memorization_request.key").noargs();
    }@Bean
    Binding responseBinding(Queue memorizationResponseQueue, Exchange memorizationExchange){
        return BindingBuilder.bind(memorizationResponseQueue).to(memorizationExchange).with("memorization_response.key").noargs();
    }


    @Autowired
    private RabbitTemplate rabbitTemplate;



    @RabbitListener(queues = memorizationRequestQueue)
    public void listen(String message,
                       @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                       @Header(AmqpHeaders.REPLY_TO) String replyToQueue){
        System.out.println("Received message: " + message);
        System.out.println("CorrelationId: " + correlationId);

        // Обработка сообщения и отправка ответа с тем же CorrelationId
        String responseMessage = "Processed message: " + message;

        // Отправляем ответ в replyTo очередь
        rabbitTemplate.convertAndSend(replyToQueue, responseMessage, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            System.out.println("Ответ отпрвален");// Устанавливаем тот же CorrelationId
            return messagePostProcessor;
        });
    }
}
