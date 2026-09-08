package com.spring.ai.api.controlller;

import com.spring.ai.api.request.ChatRequest;
import com.spring.ai.api.response.ChatResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatController {

    private final ChatClient chatClient;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder.build();
    }

    @PostMapping("/chat")
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String response = chatClient
                .prompt()
                .user(request.message())
                .call()
                .content();

        return new ChatResponse(
                true,
                response,
                "gpt-5.6-sol-2"
        );
    }
}