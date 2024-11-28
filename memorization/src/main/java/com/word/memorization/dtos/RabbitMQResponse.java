package com.word.memorization.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RabbitMQResponse {
    private String message;
    private int status;
}
