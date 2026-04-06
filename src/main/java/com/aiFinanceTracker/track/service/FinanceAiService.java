package com.aiFinanceTracker.track.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.enums.Category;
import com.aiFinanceTracker.track.interfaces.FinanceSummaryService;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;

@Service
public class FinanceAiService {

    private final ChatClient chatClient;
    private final ExpenditureRepository expRepo;
    private final FinanceSummaryService financeSummaryService;

    public FinanceAiService(ChatClient chatClient,
                            ExpenditureRepository expRepo,
                            FinanceSummaryService financeSummaryService) {
        this.chatClient = chatClient;
        this.expRepo = expRepo;
        this.financeSummaryService = financeSummaryService;
    }

    public String analyzeSpending(LocalDate from, LocalDate to) {
        List<Expenditure> exps = expRepo.findByDateBetween(from, to);
        if (exps.isEmpty()) {
            return "No expenses found in the selected period.";
        }

        String summary = buildTextSummary(from, to);

        String prompt = """
            You are a helpful personal finance coach.
            Analyze the following spending summary and give:
            - 3–5 key insights about the user's habits
            - 3 actionable suggestions to save more next month
            Be specific and concise.

            Spending summary:
            %s
            """.formatted(summary);

        return chatClient
                .prompt()
                .user(prompt)
                .call()
                .content();
    }

    private String buildTextSummary(LocalDate from, LocalDate to) {
        BigDecimal totalExpenses = financeSummaryService.getTotalExpenses(from, to);
        BigDecimal totalIncome = financeSummaryService.getTotalIncome(from, to);
        BigDecimal netSavings = financeSummaryService.getNetSavings(from, to);
        Map<Category, BigDecimal> byCategory =
                financeSummaryService.getSpendingByCategory(from, to);

        StringBuilder sb = new StringBuilder();
        sb.append("Period: ").append(from).append(" to ").append(to).append("\n");
        sb.append("Total income: ").append(totalIncome).append("\n");
        sb.append("Total expenses: ").append(totalExpenses).append("\n");
        sb.append("Net savings: ").append(netSavings).append("\n");
        sb.append("Spending by category:\n");

        byCategory.forEach((cat, amount) -> {
            sb.append("- ").append(cat.name())
              .append(": ").append(amount).append("\n");
        });

        return sb.toString();
    }
}