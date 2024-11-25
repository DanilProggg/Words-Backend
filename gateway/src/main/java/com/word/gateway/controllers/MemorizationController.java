package com.word.gateway.controllers;

import com.word.gateway.configs.RabbitMQMemorizationConfiguration;
import com.word.gateway.dtos.WordDto;
import com.word.gateway.services.JwtService;
import com.word.gateway.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api/v1/crud")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class MemorizationController {
    private final JwtService jwtService;
    private final UserService service;
    private final RabbitTemplate rabbitTemplate;
    private final Map<String, CompletableFuture<String>> responseMap = new ConcurrentHashMap<>();

    @GetMapping("/create")
    public ResponseEntity<?> rmq(@RequestBody WordDto wordDto) {
        String correlationId = UUID.randomUUID().toString();

       Long userId = (long) jwtService.extractUserId(jwtService.getJwtFromContext());

        // Создаем CompletableFuture для ожидания ответа
        CompletableFuture<String> futureResponse = new CompletableFuture<>();
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
            String response = futureResponse.get(5, TimeUnit.SECONDS);
            return ResponseEntity.ok(response);
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
