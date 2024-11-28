package com.word.memorization.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word.memorization.configs.RabbitMQConfiguration;
import com.word.memorization.dtos.RabbitMQResponse;
import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.WordAlreadyExistsException;
import com.word.memorization.services.CrudService;
import com.word.memorization.services.LearnService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
public class CrudController {

    @Autowired
    private CrudService crudService;
    @Autowired
    private LearnService learnService;
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
        System.out.println("Got message");

        try {
            System.out.println("Trying to get words");
            List<Word> words = crudService.getWords(page, userId);
            log.info("UserId: "+ userId + " Get list of words: " + words);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(words);

            rabbitReply(replyToQueue, correlationId, json, HttpStatus.OK.value());
            System.out.println("Replied");
        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            String response = String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()
            );

            rabbitReply(replyToQueue, correlationId, response, HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
    }


    /*@PostMapping("/add")
    @Operation(summary = "Add word")
    public ResponseEntity<?> addWord(@RequestBody @Valid WordDto wordDto){
        try{

            crudService.addWord(wordDto, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(new JsonResponse("The Word was added successful"));

        } catch (WordAlreadyExistsException e) {

            log.error(String.format("Word \"%s\" already exists. Error: %s",
                            wordDto.getWord(), e.getMessage()));

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    String.format("Word \"%s\" already exists. Error: %s",
                            wordDto.getWord(), e.getMessage())
            );

        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An unexpected error occurred. Error: %s",
                            e.getMessage())
            );

        }
    }
    @GetMapping("/get/all/{page}")
    @Operation(description = "Get page with user`s words")
    public ResponseEntity<?> getUsersWords(@PathVariable int page){
        try {
            List<Word> list = crudService.getWords(page, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(list);
        } catch (Exception e) {
            log.error(String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/update")
    @Operation(description = "Update word")
    public ResponseEntity<?> updateWord(@RequestBody @Valid WordDto wordDto){
        try {
            crudService.updateWord(wordDto, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(new JsonResponse("The word was updated successfully"));

        } catch (WordDoesNotExistsException e) {
            log.error(String.format("Word \"%s\"does not exist. Error: %s",
                    wordDto.getWord(), e.getMessage()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error(String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @DeleteMapping("/delete/{word}")
    @Operation(description = "Delete word")
    public ResponseEntity<?> deleteWord(@PathVariable String word){
        try {
            crudService.deleteWord(word, jwtTokenProvider.getClaims());
            return ResponseEntity.ok(new JsonResponse("The word was deleted successfully"));

        } catch (WordDoesNotExistsException e) {
            log.error(String.format("Word \"%s\"does not exist. Error: %s",
                    word, e.getMessage()));
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());

        } catch (Exception e) {
            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }*/
}
