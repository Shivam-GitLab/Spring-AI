package com.spring.ai.api.response;

public record ChatResponse(
        boolean success,
        String message,
        String model
) {
}