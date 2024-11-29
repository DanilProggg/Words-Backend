package com.word.memorization.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.word.memorization.configs.RabbitMQConfiguration;
import com.word.memorization.dtos.RabbitMQResponse;
import com.word.memorization.dtos.WordDto;
import com.word.memorization.entities.Word;
import com.word.memorization.exceptions.WordDoesNotExistsException;
import com.word.memorization.services.LearnService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/learn")
@Slf4j
@Tag(name = "Learning Controller")
public class LearnController {
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

    @RabbitListener(queues = RabbitMQConfiguration.LEARN_GET_WORD)
    public void listenCrudDelete(@Header(AmqpHeaders.CORRELATION_ID) String correlationId,
                                 @Header(AmqpHeaders.REPLY_TO) String replyToQueue,
                                 @Header("user-id") Long userId){

        try {
            Word word = learnService.getWord(userId);
            log.info("UserId: "+ userId + " Get Word to learn: " + word);

            ObjectMapper objectMapper = new ObjectMapper();
            String json = objectMapper.writeValueAsString(word);

            rabbitReply(replyToQueue, correlationId, json, HttpStatus.OK.value());

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


    /*@Autowired
    private LearnService learnService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @GetMapping("/get")
    @Operation(summary = "Get next word to learn")
    public ResponseEntity<?> learnWord(){
        try {
            return ResponseEntity.ok(learnService.getWord(jwtTokenProvider.getClaims()));

        } catch (WordDoesNotExistsException e) {

            log.error(String.format("Words to learn are ended. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.CONFLICT).body(
                    String.format("Words to learn are ended. Error: %s",
                            e.getMessage())
            );

        } catch (Exception e) {

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
        }
    }

    @Operation(summary = "Get word`s list by knowledge")
    @GetMapping("/knowledgeLevel/{level}/{page}")
    public ResponseEntity<?> getWordsByKnowledgeLevel(@PathVariable int level,
                                                      @PathVariable int page){
        try {
            return ResponseEntity.ok(learnService.getWordsByKnowledgeLevel(page, level, jwtTokenProvider.getClaims()));

        } catch (Exception e) {

            log.error(String.format("An unexpected error occurred. Error: %s",
                    e.getMessage()));

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    String.format("An unexpected error occurred. Error: %s",
                            e.getMessage()));
        }
    }*/
}
