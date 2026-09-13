package com.spring.ai.api.controlller;

import com.spring.ai.api.dtos.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class AdvisorController {
    private final ChatClient chatClient;

    @GetMapping("/advise")
    public ChatResponse advisor(@RequestParam String message){
        String response = chatClient
                .prompt()
                .user(message)
                .call()
                .content();
        return new ChatResponse(
                true,
                response,
                "gpt-5.6-sol-2"
        );
    }
}
