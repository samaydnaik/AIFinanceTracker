package com.aiFinanceTracker.track.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.aiFinanceTracker.track.enums.Category;
import com.aiFinanceTracker.track.interfaces.FinanceSummaryService;
import org.springframework.stereotype.Service;

@Service
public class FinanceToolService {

    private final FinanceSummaryService summaryService;

    public FinanceToolService(FinanceSummaryService summaryService) {
        this.summaryService = summaryService;
    }

    public Map<Category, BigDecimal> getSpendingByCategory(LocalDate from, LocalDate to) {
        return summaryService.getSpendingByCategory(from, to);
    }

    public BigDecimal getTotalIncome(LocalDate from, LocalDate to) {
        return summaryService.getTotalIncome(from, to);
    }

    public BigDecimal getTotalExpenses(LocalDate from, LocalDate to) {
        return summaryService.getTotalExpenses(from, to);
    }

    public BigDecimal getNetSavings(LocalDate from, LocalDate to) {
        return summaryService.getNetSavings(from, to);
    }
}