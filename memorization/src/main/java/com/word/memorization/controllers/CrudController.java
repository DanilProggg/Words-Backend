package com.word.memorization.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word.memorization.configs.RabbitMQConfiguration;
import com.word.memorization.dtos.RabbitMQResponse;
import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.WordAlreadyExistsException;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.services.CrudService;
import com.word.memorization.services.LearnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CrudController {

    @Autowired
    private CrudService crudService;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    private void rabbitReply(String replyToQueue, String correlationId, String response, int status) {
        rabbitTemplate.convertAndSend(replyToQueue, new RabbitMQResponse(response, status), messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            return messagePostProcessor;
        });
    }



    @RabbitListener(queues = RabbitMQConfiguration.CRUD_CREATE)
    public void listenCrudCreate(WordDto wordDto,
                       @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                       @Header(AmqpHeaders.REPLY_TO) String replyToQueue,
                       @Header("user-id") Long userId){

        try {
            crudService.addWord(wordDto, userId);
            log.info("UserId: "+ userId + " Added word: " + wordDto.toString());

            String response = "The Word was added successful";

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.OK.value());
        } catch (WordAlreadyExistsException e) {

            log.error(String.format("Word \"%s\" already exists. Error: %s",
                    wordDto.getWord(), e.getMessage()));

            String response = String.format("Word \"%s\" already exists. Error: %s",
                            wordDto.getWord(), e.getMessage()
            );

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.CONFLICT.value());
        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            String response = String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()
            );

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }




    @RabbitListener(queues = RabbitMQConfiguration.CRUD_GET)
    public void listenCrudGet(int page,
                              @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                              @Header(AmqpHeaders.REPLY_TO) String replyToQueue,
                              @Header("user-id") Long userId){

        try {
            List<Word> words = crudService.getWords(page, userId);
            log.info("UserId: "+ userId + " Get list of words: " + words);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(words);

            rabbitReply(replyToQueue, correlationId, json, HttpStatus.OK.value());
        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            String response = String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()
            );

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    @RabbitListener(queues = RabbitMQConfiguration.CRUD_UPDATE)
    public void listenCrudUpdate(WordDto wordDto,
                              @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                              @Header(AmqpHeaders.REPLY_TO) String replyToQueue,
                              @Header("user-id") Long userId){

        try {
            crudService.updateWord(wordDto, userId);
            log.info("UserId: "+ userId + " Update word: " + wordDto);

            String response = "Word updated";

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.OK.value());

        } catch (WordDoesNotExistsException e) {
            log.error(e.getMessage());
            String response = e.getMessage()
                    ;
            rabbitReply(replyToQueue, correlationId, response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            String response = String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()
            );

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }

    @RabbitListener(queues = RabbitMQConfiguration.CRUD_DELETE)
    public void listenCrudDelete(WordDto wordDto,
                                 @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                 @Header(AmqpHeaders.REPLY_TO) String replyToQueue,
                                 @Header("user-id") Long userId){

        try {
            crudService.deleteWord(wordDto, userId);
            log.info("UserId: "+ userId + " Delete word: " + wordDto);

            String response = "Word deleted";

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.OK.value());

        } catch (WordDoesNotExistsException e) {
            log.error(e.getMessage());
            String response = e.getMessage();
            rabbitReply(replyToQueue, correlationId, response, HttpStatus.CONFLICT.value());
        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            String response = String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()
            );

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }
}
