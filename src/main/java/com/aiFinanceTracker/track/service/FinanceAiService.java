package com.aiFinanceTracker.track.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.aiFinanceTracker.track.dto.SpendingAnalysisResult;
import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.enums.Category;
import com.aiFinanceTracker.track.interfaces.FinanceSummaryService;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class FinanceAiService {

    private final ChatClient chatClient;
    private final ExpenditureRepository expRepo;
    private final FinanceSummaryService financeSummaryService;
    private final ObjectMapper objectMapper;

    public FinanceAiService(ChatClient chatClient,
                            ExpenditureRepository expRepo,
                            FinanceSummaryService financeSummaryService,
                            ObjectMapper objectMapper) {
        this.chatClient = chatClient;
        this.expRepo = expRepo;
        this.financeSummaryService = financeSummaryService;
        this.objectMapper = objectMapper;
    }

    public SpendingAnalysisResult analyzeSpending(LocalDate from, LocalDate to) {
        List<Expenditure> exps = expRepo.findByDateBetween(from, to);
        if (exps.isEmpty()) {
            SpendingAnalysisResult empty = new SpendingAnalysisResult();
            empty.setSummary("No expenses found in the selected period.");
            empty.setTopIssues(List.of());
            empty.setRecommendations(List.of());
            empty.setScore(null);
            return empty;
        }

        BigDecimal totalExpenses = financeSummaryService.getTotalExpenses(from, to);
        BigDecimal totalIncome = financeSummaryService.getTotalIncome(from, to);
        BigDecimal netSavings = financeSummaryService.getNetSavings(from, to);
        Map<Category, BigDecimal> byCategory =
                financeSummaryService.getSpendingByCategory(from, to);

        String prompt = buildAnalysePrompt(from, to, totalIncome, totalExpenses, netSavings, byCategory);

        String raw = chatClient
                .prompt()
                .user(prompt)
                .call()
                .content(); // standard ChatClient usage[web:31][web:34]

        return parseAnalysisResult(raw);
    }

    private String buildAnalysePrompt(LocalDate from,
                                      LocalDate to,
                                      BigDecimal totalIncome,
                                      BigDecimal totalExpenses,
                                      BigDecimal netSavings,
                                      Map<Category, BigDecimal> byCategory) {

        StringBuilder categories = new StringBuilder();
        byCategory.forEach((cat, amount) -> {
            categories.append("- ").append(cat.name())
                    .append(": ₹").append(amount).append("\n");
        });

        return """
You are a helpful personal finance advisor for an Indian salaried person.
Analyse this person's spending in INR for the given period and give very concrete, practical advice.

Period: %s to %s
Total income: ₹%s
Total expenses: ₹%s
Net savings: ₹%s

Spending by category:
%s

Return your answer ONLY as RFC8259-compliant JSON matching this Java class:

class SpendingAnalysisResult {
  String summary;                // 2-3 sentence overview of their situation
  List<String> topIssues;        // up to 3 specific spending problems
  List<String> recommendations;  // up to 5 concrete, actionable tips
  Integer score;                 // 0-10, where 10 = excellent financial health
}

Do not include markdown, code fences, comments, or explanations.
Just return the JSON object.
""".formatted(from, to, totalIncome, totalExpenses, netSavings, categories);
    }

    private SpendingAnalysisResult parseAnalysisResult(String raw) {
        try {
            String cleaned = raw.trim();
            if (cleaned.startsWith("```")) {
                // handle ```json ... ``` or ``` ... ```
                int firstNewline = cleaned.indexOf('\n');
                int lastFence = cleaned.lastIndexOf("```");
                if (firstNewline != -1 && lastFence != -1 && lastFence > firstNewline) {
                    cleaned = cleaned.substring(firstNewline + 1, lastFence).trim();
                }
            }
            return objectMapper.readValue(cleaned, SpendingAnalysisResult.class); // Jackson mapping pattern[web:13][web:35]
        } catch (Exception e) {
            SpendingAnalysisResult fallback = new SpendingAnalysisResult();
            fallback.setSummary("AI could not return structured JSON. Raw response:\n\n" + raw);
            fallback.setTopIssues(List.of());
            fallback.setRecommendations(List.of());
            fallback.setScore(null);
            return fallback;
        }
    }
}