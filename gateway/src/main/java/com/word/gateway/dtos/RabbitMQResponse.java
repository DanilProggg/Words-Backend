package com.word.gateway.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RabbitMQResponse {
    private String message;
    private int status;
}
