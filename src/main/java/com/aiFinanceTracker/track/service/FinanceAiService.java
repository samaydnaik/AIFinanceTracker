package com.aiFinanceTracker.track.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class FinanceAiService {

    private final ChatClient chatClient;
    private final ExpenditureRepository expRepo;

    public FinanceAiService(ChatClient chatClient,
                            ExpenditureRepository expRepo) {
        this.chatClient = chatClient;
        this.expRepo = expRepo;
    }

    public String analyzeSpending(LocalDate from, LocalDate to) {
        List<Expenditure> exps = expRepo.findByDateBetween(from, to);
        if (exps.isEmpty()) {
            return "No expenses found in the selected period.";
        }

        String summary = buildTextSummary(exps);

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
                .prompt()          // start a prompt
                .user(prompt)      // set user message
                .call()            // execute
                .content();        // get String response
    }

    private String buildTextSummary(List<Expenditure> exps) {
        Map<String, Double> byCategory = exps.stream()
            .collect(Collectors.groupingBy(
                e -> e.getCategory().name(),
                Collectors.summingDouble(e -> e.getAmount().doubleValue())
            ));

        double total = exps.stream()
            .mapToDouble(e -> e.getAmount().doubleValue())
            .sum();

        StringBuilder sb = new StringBuilder();
        sb.append("Total expenses: ").append(total).append("\n");
        byCategory.forEach((cat, sum) ->
            sb.append("Category ").append(cat)
              .append(": ").append(sum).append("\n")
        );
        return sb.toString();
    }
}
