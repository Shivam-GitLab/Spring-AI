package com.spring.ai.api.dtos.response;

import java.util.List;

public record ActorsFilms(
        String actor,
        List<String> movies
) {
}