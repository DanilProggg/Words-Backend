package com.word.gateway.configs;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQMemorizationConfiguration {
    //Queue for responses form memorization service

    public static final String CRUD_CREATE  = "memorization.crud.create.queue";
    public static final String CRUD_DELETE  = "memorization.crud.delete.queue";
    public static final String CRUD_UPDATE  = "memorization.crud.update.queue";
    public static final String CRUD_GET  = "memorization.crud.get.queue";
    public static final String LEARN_GET_WORD  = "memorization.learn.get.word.queue";
    public static final String LEARN_GET_WORD_BY_KNOWLEDGE = "memorization.learn.get.words_by_knowledge.queue";
    public static final String LEARN_UPDATE_WORD_BY_KNOWLEDGE = "memorization.learn.update.word_by_knowledge.queue";
    public static final String RESPONSE_QUEUE  = "memorization_response.queue";
    public static final String EXCHANGE  = "memorization_exchange";

    @Bean
    public Queue memorizationCrudCreateQueue(){
        return new Queue(CRUD_CREATE, true);
    }
    @Bean
    public Queue memorizationCrudDeleteQueue(){
        return new Queue(CRUD_DELETE, true);
    }
    @Bean
    public Queue memorizationCrudUpdateQueue(){
        return new Queue(CRUD_UPDATE, true);
    }
    @Bean
    public Queue memorizationCrudGetQueue(){
        return new Queue(CRUD_GET, true);
    }
    @Bean
    public Queue memorizationResponseQueue(){
        return new Queue(RESPONSE_QUEUE, true);
    }

    @Bean
    public Queue memorizationLearnGetWordQueue(){
        return new Queue(LEARN_GET_WORD, true);
    }
    @Bean
    public Queue memorizationLearnGetWordByKnowledgeQueue(){
        return new Queue(LEARN_GET_WORD_BY_KNOWLEDGE, true);
    }
    @Bean
    public Queue memorizationLearnUpdateWordKnowledgeQueue(){
        return new Queue(LEARN_UPDATE_WORD_BY_KNOWLEDGE, true);
    }
    @Bean
    Exchange memorizationExchange() {
        return new DirectExchange(EXCHANGE, true, false);
    }

    @Bean
    Binding bindingCrudCreate(Queue memorizationCrudCreateQueue, Exchange memorizationExchange){
        return BindingBuilder.bind(memorizationCrudCreateQueue)
                .to(memorizationExchange)
                .with("crud.create")
                .noargs();
    }
    @Bean
    Binding bindingCrudDelete(Queue memorizationCrudDeleteQueue, Exchange memorizationExchange){
        return BindingBuilder.bind(memorizationCrudDeleteQueue)
                .to(memorizationExchange)
                .with("crud.delete")
                .noargs();
    }

    @Bean
    Binding bindingCrudUpdate(Queue memorizationCrudUpdateQueue, Exchange memorizationExchange) {
        return BindingBuilder.bind(memorizationCrudUpdateQueue)
                .to(memorizationExchange())
                .with("crud.update")
                .noargs();
    }

    @Bean
    Binding bindingCrudGet(Queue memorizationCrudGetQueue, Exchange memorizationExchange) {
        return BindingBuilder.bind(memorizationCrudGetQueue)
                .to(memorizationExchange())
                .with("crud.get")
                .noargs();
    }

}
