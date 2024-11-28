package com.word.gateway.controllers;

import com.word.gateway.configs.RabbitMQMemorizationConfiguration;
import com.word.gateway.dtos.RabbitMQResponse;
import com.word.gateway.dtos.WordDto;
import com.word.gateway.services.JwtService;
import com.word.gateway.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class MemorizationController {
    private final JwtService jwtService;
    private final UserService service;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, CompletableFuture<RabbitMQResponse>> responseMap = new ConcurrentHashMap<>();

    @GetMapping("/crud/add")
    @Operation(description = "Add word to database for exactly user")
    public ResponseEntity<?> create(@RequestBody WordDto wordDto) {
        String correlationId = UUID.randomUUID().toString();

        Long userId = (long) jwtService.extractUserId(jwtService.getJwtFromContext());

        // Создаем CompletableFuture для ожидания ответа
        CompletableFuture<RabbitMQResponse> futureResponse = new CompletableFuture<>();
        responseMap.put(correlationId, futureResponse);

        // Отправляем сообщение с CorrelationId
        rabbitTemplate.convertAndSend(RabbitMQMemorizationConfiguration.EXCHANGE, "crud.create", wordDto, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            messagePostProcessor.getMessageProperties().setReplyTo(RabbitMQMemorizationConfiguration.RESPONSE_QUEUE); // Указываем очередь для ответа
            messagePostProcessor.getMessageProperties().setHeader("user-id", userId);
            return messagePostProcessor;
        });

        // Ожидаем ответа асинхронно
        try {
            // Установите тайм-аут ожидания ответа
            RabbitMQResponse response = futureResponse.get(5, TimeUnit.SECONDS);

            return new ResponseEntity<>(response.getMessage(), HttpStatus.valueOf(response.getStatus()));
        } catch (TimeoutException e) {
            responseMap.remove(correlationId);  // Delete if time out
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out.");
        } catch (InterruptedException | ExecutionException e) {
            responseMap.remove(correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }


    @GetMapping("/crud/get/{page}")
    @Operation(description = "Get list with words for exactly user")
    public ResponseEntity<?> get(@PathVariable int page) {
        String correlationId = UUID.randomUUID().toString();

        Long userId = (long) jwtService.extractUserId(jwtService.getJwtFromContext());

        // Создаем CompletableFuture для ожидания ответа
        CompletableFuture<RabbitMQResponse> futureResponse = new CompletableFuture<>();
        responseMap.put(correlationId, futureResponse);

        // Отправляем сообщение с CorrelationId
        rabbitTemplate.convertAndSend(RabbitMQMemorizationConfiguration.EXCHANGE, "crud.get", page, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            messagePostProcessor.getMessageProperties().setReplyTo(RabbitMQMemorizationConfiguration.RESPONSE_QUEUE); // Указываем очередь для ответа
            messagePostProcessor.getMessageProperties().setHeader("user-id", userId);
            return messagePostProcessor;
        });

        // Ожидаем ответа асинхронно
        try {
            // Установите тайм-аут ожидания ответа
            RabbitMQResponse response = futureResponse.get(5, TimeUnit.SECONDS);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.valueOf(response.getStatus()));
        } catch (TimeoutException e) {
            responseMap.remove(correlationId);  // Delete if time out
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out.");
        } catch (InterruptedException | ExecutionException e) {
            responseMap.remove(correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @PostMapping("/crud/update")
    @Operation(description = "Update word")
    public ResponseEntity<?> updateWord(@RequestBody @Valid WordDto wordDto){
        String correlationId = UUID.randomUUID().toString();

        Long userId = (long) jwtService.extractUserId(jwtService.getJwtFromContext());

        // Создаем CompletableFuture для ожидания ответа
        CompletableFuture<RabbitMQResponse> futureResponse = new CompletableFuture<>();
        responseMap.put(correlationId, futureResponse);

        // Отправляем сообщение с CorrelationId
        rabbitTemplate.convertAndSend(RabbitMQMemorizationConfiguration.EXCHANGE, "crud.update", wordDto, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            messagePostProcessor.getMessageProperties().setReplyTo(RabbitMQMemorizationConfiguration.RESPONSE_QUEUE); // Указываем очередь для ответа
            messagePostProcessor.getMessageProperties().setHeader("user-id", userId);
            return messagePostProcessor;
        });

        // Ожидаем ответа асинхронно
        try {
            // Установите тайм-аут ожидания ответа
            RabbitMQResponse response = futureResponse.get(5, TimeUnit.SECONDS);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.valueOf(response.getStatus()));
        } catch (TimeoutException e) {
            responseMap.remove(correlationId);  // Delete if time out
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out.");
        } catch (InterruptedException | ExecutionException e) {
            responseMap.remove(correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    @DeleteMapping("/crud/delete")
    @Operation(description = "Update word")
    public ResponseEntity<?> deleteWord(@RequestBody @Valid WordDto wordDto){
        String correlationId = UUID.randomUUID().toString();

        Long userId = (long) jwtService.extractUserId(jwtService.getJwtFromContext());

        // Создаем CompletableFuture для ожидания ответа
        CompletableFuture<RabbitMQResponse> futureResponse = new CompletableFuture<>();
        responseMap.put(correlationId, futureResponse);

        // Отправляем сообщение с CorrelationId
        rabbitTemplate.convertAndSend(RabbitMQMemorizationConfiguration.EXCHANGE, "crud.delete", wordDto, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            messagePostProcessor.getMessageProperties().setReplyTo(RabbitMQMemorizationConfiguration.RESPONSE_QUEUE); // Указываем очередь для ответа
            messagePostProcessor.getMessageProperties().setHeader("user-id", userId);
            return messagePostProcessor;
        });

        // Ожидаем ответа асинхронно
        try {
            // Установите тайм-аут ожидания ответа
            RabbitMQResponse response = futureResponse.get(5, TimeUnit.SECONDS);
            return new ResponseEntity<>(response.getMessage(), HttpStatus.valueOf(response.getStatus()));
        } catch (TimeoutException e) {
            responseMap.remove(correlationId);  // Delete if time out
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out.");
        } catch (InterruptedException | ExecutionException e) {
            responseMap.remove(correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    public void completeResponse(String correlationId, RabbitMQResponse response) {
        CompletableFuture<RabbitMQResponse> future = responseMap.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

}
