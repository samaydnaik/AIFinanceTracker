package com.aiFinanceTracker.track.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        // You can set a default system prompt here if you like
        return builder
                .defaultSystem("You are a helpful personal finance coach.")
                .build();
    }
}
