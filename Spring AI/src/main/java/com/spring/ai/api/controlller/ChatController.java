package com.spring.ai.api.controlller;

import com.spring.ai.api.request.ChatRequest;
import com.spring.ai.api.response.ChatResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;
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

        /*  Your Controller handler
        → ChatClient.prompt().user("...").call()
            → Spring AI builds a Prompt object
                → OpenAiChatModel sends HTTP POST to api.openai.com/v1/chat/completions
                    → Response is parsed into ChatResponse
                        → .content() extracts the text*/

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

    // PROMPT TEMPLATE
    // INJECT SOME VARIABLES INTO THE PROMPT
    // AND CHANGE PROMPT DYNAMICALLY BASED ON USER INPUT
    @GetMapping("/chat-with-template")
    public String chatTemplate(@RequestParam String domain, @RequestParam String question) {
        String promptTemplate = """
                You are a world-class expert in {domain}.
                Answer the following question clearly and concisely.
                
                Question: {question}
                """;
        return chatClient
                .prompt()
                .user(u -> u.text(promptTemplate)
                        .param("domain", domain)
                        .param("question", question)
                )
                .call()
                .content();
    }
}