package com.aiFinanceTracker.track.controller;

import java.time.LocalDate;

import com.aiFinanceTracker.track.dto.SpendingAnalysisResult;
import com.aiFinanceTracker.track.service.FinanceAiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FinanceAiController {

    private final FinanceAiService aiService;

    public FinanceAiController(FinanceAiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/api/ai/analyze")
    public SpendingAnalysisResult analyze(
            @RequestParam String from,
            @RequestParam String to) {

        LocalDate start = LocalDate.parse(from);
        LocalDate end = LocalDate.parse(to);
        return aiService.analyzeSpending(start, end);
    }
}