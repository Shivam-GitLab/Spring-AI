package com.spring.ai.api.dtos.response;

public record ChatResponse(
        boolean success,
        String message,
        String model
) {
}