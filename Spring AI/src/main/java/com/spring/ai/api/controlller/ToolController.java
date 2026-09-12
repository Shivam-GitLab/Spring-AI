package com.spring.ai.api.controlller;

import com.spring.ai.api.dtos.response.ChatResponse;
import com.spring.ai.service.UserService;
import com.spring.ai.tools.WeatherTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class ToolController {

    private final ChatClient chatClient;
    private final WeatherTool weatherTool;
    private final UserService userService;

    @Value("classpath:/prompts/promptweather.st")
    private Resource promptResourceWeather;

    @Value("classpath:/prompts/promptuser.st")
    private Resource promptResourceUser;

    @Value("classpath:/prompts/WeatherUserData.st")
    private Resource getPromptResourceWeatherUser;


    public ToolController(ChatClient.Builder builder, WeatherTool weatherTool, UserService userService) {
        this.chatClient = builder.build();
        this.weatherTool = weatherTool;
        this.userService = userService;
    }

    @GetMapping("/weather-agent")
    public ChatResponse getWeatherHandler(@RequestParam(name = "query") String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptResourceWeather);
        Prompt prompt = promptTemplate.create();
        String response = chatClient
                .prompt(prompt)
                .user(query)
                .tools(weatherTool, userService)
                .call()
                .content();
        return new ChatResponse(
                true,
                response,
                "gpt-5.6-sol-2"
        );
    }

    @GetMapping("/user")
    public ChatResponse getUserInfoHandler(@RequestParam(name = "query") String query) {
        PromptTemplate promptTemplate = new PromptTemplate(promptResourceUser);
        Prompt prompt = promptTemplate.create();
        String response =  chatClient
                .prompt(prompt)
                .user(query)
                .tools(userService)
                .call()
                .content();

        return new ChatResponse(
                true,
                response,
                "gpt-5.6-sol-2"
        );
    }

    @GetMapping("/weather-user-agent")
    public ChatResponse getWeatherUserBoth(@RequestParam(name = "query") String query) {
        PromptTemplate promptTemplate = new PromptTemplate(getPromptResourceWeatherUser);
        Prompt prompt = promptTemplate.create();
        String response = chatClient
                .prompt(prompt)
                .user(query)
                .tools(weatherTool, userService)
                .call()
                .content();
        return new ChatResponse(
                true,
                response,
                "gpt-5.6-sol-2"
        );
    }

}
