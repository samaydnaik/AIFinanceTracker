package com.aiFinanceTracker.track.controller;

import com.aiFinanceTracker.track.service.ChatAiService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai/chat")
public class ChatAiController {

    private final ChatAiService chatAiService;

    public ChatAiController(ChatAiService chatAiService) {
        this.chatAiService = chatAiService;
    }

    public static class ChatRequest {
        public String sessionId; // optional
        public String message;
    }

    public static class ChatResponse {
        public String sessionId;
        public String reply;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {
        String sid = request.sessionId;
        String reply = chatAiService.chat(sid, request.message);

        ChatResponse res = new ChatResponse();
        res.sessionId = (sid == null || sid.isBlank()) ? "new" : sid;
        res.reply = reply;
        return res;
    }
}