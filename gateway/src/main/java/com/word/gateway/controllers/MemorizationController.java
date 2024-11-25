package com.word.gateway.controllers;

import com.word.gateway.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class MemorizationController {
    private final UserService service;
    private final RabbitTemplate rabbitTemplate;



    private final Map<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();


    private final String memorizationResponseQueue = "memorization_response_queue";
    private final String memorizationExchange = "memorization_exchange";

    @GetMapping("/rabbitmq")
    public ResponseEntity<String> rmq(@RequestParam String message) {
        String correlationId = UUID.randomUUID().toString();

        // Создаем CompletableFuture для ожидания ответа
        CompletableFuture<String> futureResponse = new CompletableFuture<>();
        responseMap.put(correlationId, futureResponse);

        // Отправляем сообщение с CorrelationId
        rabbitTemplate.convertAndSend(memorizationExchange, "memorization_request.key", message, messagePostProcessor -> {
            messagePostProcessor.getMessageProperties().setCorrelationId(correlationId);
            messagePostProcessor.getMessageProperties().setReplyTo(memorizationResponseQueue);  // Указываем очередь для ответа
            return messagePostProcessor;
        });

        // Ожидаем ответа асинхронно
        try {
            // Установите тайм-аут ожидания ответа
            String response = futureResponse.get(10, TimeUnit.SECONDS);
            return ResponseEntity.ok("Response received: " + response);
        } catch (TimeoutException e) {
            responseMap.remove(correlationId);  // Убираем из мапы, если истек тайм-аут
            return ResponseEntity.status(HttpStatus.REQUEST_TIMEOUT).body("Request timed out.");
        } catch (InterruptedException | ExecutionException e) {
            responseMap.remove(correlationId);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing request: " + e.getMessage());
        }
    }

    public void completeResponse(String correlationId, String response) {
        CompletableFuture<String> future = responseMap.remove(correlationId);
        if (future != null) {
            future.complete(response);
        }
    }

}
