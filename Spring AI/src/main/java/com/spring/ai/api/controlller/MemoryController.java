package com.spring.ai.api.controlller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
public class MemoryController {
    private final ChatClient chatClient;
    private final ChatMemory chatMemory;

    @GetMapping("/chat-memory")
    public String chatMemoryMethod(@RequestParam String message,
                                   @RequestParam String conversationId ){
        return chatClient
                .prompt()
                .user(message)
                .advisors(
                        advisorSpec ->
                        advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();

    }
}
