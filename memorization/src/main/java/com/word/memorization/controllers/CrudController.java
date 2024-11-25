package com.word.memorization.controllers;

import com.word.memorization.configs.RabbitMQConfiguration;
import com.word.memorization.dtos.JsonResponse;
import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.WordAlreadyExistsException;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.services.CrudService;
import com.word.memorization.services.LearnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

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



    @RabbitListener(queues = RabbitMQConfiguration.CRUD_CREATE)
    public void listen(WordDto wordDto,
                       @Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                       @Header(AmqpHeaders.REPLY_TO) String replyToQueue,
                       @Header("user-id") Long userId){
        System.out.println("Received message" + wordDto.toString());
        System.out.println("CorrelationId: " + correlationId);
        System.out.println("UserId: " + userId);




        try{

            crudService.addWord(wordDto, userId);
            log.info("UserId: "+ userId + " Added word: " + wordDto.toString());

            String response = "The Word was added successful";

            rabbitTemplate.convertAndSend(replyToQueue, response, messagePostProcessor -> {
                messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
                messagePostProcessor.getMessageProperties().setHeader("status", HttpStatus.OK);
                return messagePostProcessor;
            });

        } catch (WordAlreadyExistsException e) {

            log.error(String.format("Word \"%s\" already exists. Error: %s",
                    wordDto.getWord(), e.getMessage()));


            String response = String.format("Word \"%s\" already exists. Error: %s",
                            wordDto.getWord(), e.getMessage()
            );

            rabbitTemplate.convertAndSend(replyToQueue, response, messagePostProcessor -> {
                messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
                messagePostProcessor.getMessageProperties().setHeader("status", HttpStatus.CONFLICT);
                return messagePostProcessor;
            });

        } catch (Exception e){

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));


            String response = String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()
            );

            rabbitTemplate.convertAndSend(replyToQueue, response, messagePostProcessor -> {
                messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
                messagePostProcessor.getMessageProperties().setHeader("status", HttpStatus.CONFLICT);
                return messagePostProcessor;
            });

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
