package com.spring.ai.api.controlller;

import com.spring.ai.api.dtos.request.ChatRequest;
import com.spring.ai.api.dtos.response.ChatResponse;
import com.spring.ai.api.dtos.response.LanguageAnalysis;
import com.spring.ai.api.dtos.response.ProjectIdea;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import com.spring.ai.api.dtos.response.ActorsFilms;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@Slf4j
public class ChatController {

    private final ChatClient chatClient;

    @Value("classpath:/prompts/prompt.st")
    private Resource promptResource;

    public ChatController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultSystem("You are a helpful AI assistant. You are expected to answer the questions related to the given domain.\" +\n" +
                        "                        \"Any question that is asked outside of the domain, decline it by responding to user that \" +\n" +
                        "                        \"'I specialize in the given domain and I can't answer any query outside it'.")
                .build();
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

    @GetMapping("/chat-with-inbuilt-template-method")
    public String chatInBuiltTemplateMethod(@RequestParam String domain, @RequestParam String question) {
        PromptTemplate promptTemplate = new PromptTemplate(promptResource);

        Prompt prompt = promptTemplate.create(Map.of("domain", domain, "question", question));
        return chatClient
                .prompt(prompt)
                .call()
                .content();

    }

    @GetMapping(value = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream() {
        return chatClient
                .prompt()
                .user("What is Stream in ChatClient")
                .stream()
                .content();
    }

    @GetMapping("/structured-response")
    public ActorsFilms structuredResponse(){
        ActorsFilms entity = chatClient
                .prompt()
                .user("Generate the to 10 filmography for a random actor.")
                .call()
                .entity(ActorsFilms.class);

        log.info("entity : {}", entity);

        return entity;
    }

    @GetMapping("/analyze-language")
    public LanguageAnalysis analyzeLanguage(@RequestParam String language) {

        BeanOutputConverter<LanguageAnalysis> converter = new BeanOutputConverter<>(LanguageAnalysis.class);

        return chatClient
                .prompt()
                .user(u -> u.text("""
                        Analyze the programming language: {language}
                        
                        popularity score: between 1-10, 1 is low, 10 is high
                        learningDifficulty: either of these 3 values: "Easy", "Medium", "Hard"
                        {format}
                        """)
                        .param("language", language)
                        .param("format", converter.getFormat()))
                .call()
                .entity(LanguageAnalysis.class);
    }

    @GetMapping("/suggest-projects")
    public List<ProjectIdea> suggestProjects(@RequestParam String techStack) {

        var converter = new BeanOutputConverter<>(
                new ParameterizedTypeReference<List<ProjectIdea>>() {
                }
        );

        return chatClient.prompt()
                .user(u -> u.text("""
                        Suggest 3 beginner project ideas for a developer using {techStack}.
                        
                        {format}
                        """)
                        .param("techStack", techStack)
                        .param("format", converter.getFormat()))
                .call()
                .entity(new ParameterizedTypeReference<>(){});
    }
}



