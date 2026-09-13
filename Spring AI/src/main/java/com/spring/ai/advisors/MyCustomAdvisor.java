package com.spring.ai.advisors;

import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

@Component
public class MyCustomAdvisor implements CallAdvisor {

    private static final Logger log = LoggerFactory.getLogger(MyCustomAdvisor.class);

    @Override
    public @NonNull ChatClientResponse adviseCall(@NonNull ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {

        long startTime = System.currentTimeMillis();

        ChatClientResponse response = callAdvisorChain.nextCall(chatClientRequest);

        long duration = System.currentTimeMillis() - startTime;

        log.info("AI call completed in {}ms | Prompt : {}",
                duration,
                chatClientRequest.prompt().getUserMessage().getText());

        return response;
    }

    @Override
    public @NonNull String getName() {
        return "MyCustomAdvisor";
    }

    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }
}
