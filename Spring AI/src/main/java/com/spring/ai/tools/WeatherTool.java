package com.spring.ai.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Map;

@Component
@Slf4j
public class WeatherTool {

    private final RestClient restClient;

    public WeatherTool(RestClient.Builder builder) {
        this.restClient = builder
                .baseUrl("https://api.tavily.com/search")
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    // name, description
    @Tool(description = "Get the current weather for a given city")
    public String getWeather(@ToolParam(description = "city name") String city) {
        log.info("Tool calling for getting weather...");

        Map<String, Object> response = restClient.post()
                .header("Authorization", "Bearer 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")
                .body(Map.of(
                        "query", "current weather in " + city + " today temperature conditions",
                        "include_answer", true))
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        log.info("Weather response : {}", response);

        Object answer = response != null ? response.get("answer") : null;
        return answer != null ? answer.toString() : String.valueOf(response);
    }
}