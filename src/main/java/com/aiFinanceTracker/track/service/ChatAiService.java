package com.aiFinanceTracker.track.service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Service;


@Service
public class ChatAiService {

    private final ChatClient chatClient;

    // sessionId -> conversation history
    private final ConcurrentMap<String, List<Message>> conversations = new ConcurrentHashMap<>();

    public ChatAiService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String sessionId, String userMessage) {
        if (sessionId == null || sessionId.isBlank()) {
            sessionId = UUID.randomUUID().toString();
        }

        conversations.putIfAbsent(sessionId,
                new java.util.concurrent.CopyOnWriteArrayList<>());

        List<Message> history = conversations.get(sessionId);

        if (history.isEmpty()) {
            history.add(new SystemMessage(
                    "You are a helpful personal finance assistant for an Indian salaried person. " +
                    "Remember previous questions and answers in this chat session and be concise."));
        }

        history.add(new UserMessage(userMessage));

        // ChatClient in 2.x: call().content() directly returns String[web:31]
        String reply = chatClient
                .prompt()
                .messages(history)
                .call()
                .content();

        // store assistant reply as a message in history
        history.add(new AssistantMessage(reply));

        return reply;
    }
}