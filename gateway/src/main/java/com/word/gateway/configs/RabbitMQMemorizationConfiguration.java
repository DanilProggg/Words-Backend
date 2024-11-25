package com.word.gateway.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQMemorizationConfiguration {
    //Queue for responses form memorization service

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

}
