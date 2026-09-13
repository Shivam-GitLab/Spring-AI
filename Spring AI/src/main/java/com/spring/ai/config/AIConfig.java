package com.spring.ai.config;

import com.openai.models.vectorstores.VectorStore;
import com.spring.ai.advisors.MyCustomAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SafeGuardAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class AIConfig {

    @Bean
    public ChatMemory chatMemoryTemp(){
        return MessageWindowChatMemory
                .builder()
                .maxMessages(10)  // 10 last messages at once, 11th message -> 1st removed
                .build();
    }

    @Bean
    public ChatMemory chatMemory(JdbcChatMemoryRepository repository) {
        return MessageWindowChatMemory.builder()
                .chatMemoryRepository(repository)
                .maxMessages(10)
                .build();
    }
    /*@Bean
    public ChatClient chatClient(ChatClient.Builder builder, ChatMemory chatMemory) {
        return builder
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
    }*/

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, MyCustomAdvisor myCustomAdvisor) {
        return builder
                .defaultAdvisors(
                        new SafeGuardAdvisor(List.of(
                                "security",
                                "confidential",
                                "password"
                        )),
                        new SimpleLoggerAdvisor(),
                        myCustomAdvisor
                )
                .build();
    }

}
