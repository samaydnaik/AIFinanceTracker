package com.aiFinanceTracker.track.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import com.aiFinanceTracker.track.enums.Category;

public interface FinanceSummaryService {

    BigDecimal getTotalIncome(LocalDate from, LocalDate to);

    BigDecimal getTotalExpenses(LocalDate from, LocalDate to);

    BigDecimal getNetSavings(LocalDate from, LocalDate to);

    Map<Category, BigDecimal> getSpendingByCategory(LocalDate from, LocalDate to);
}