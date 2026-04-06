package com.aiFinanceTracker.track.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.aiFinanceTracker.track.entities.Expenditure;
import com.aiFinanceTracker.track.entities.IncomeSource;
import com.aiFinanceTracker.track.enums.Category;
import com.aiFinanceTracker.track.repositories.ExpenditureRepository;
import com.aiFinanceTracker.track.repositories.IncomeSourceRepository;
import com.aiFinanceTracker.track.repositories.SavingsRepository;

@Service
public class FinanceSummaryServiceImpl implements FinanceSummaryService {

    private final IncomeSourceRepository incomeRepository;
    private final ExpenditureRepository expenditureRepository;
    private final SavingsRepository savingsRepository;

    public FinanceSummaryServiceImpl(IncomeSourceRepository incomeRepository,
                                     ExpenditureRepository expenditureRepository,
                                     SavingsRepository savingsRepository) {
        this.incomeRepository = incomeRepository;
        this.expenditureRepository = expenditureRepository;
        this.savingsRepository = savingsRepository;
    }

    @Override
    public BigDecimal getTotalIncome(LocalDate from, LocalDate to) {
        List<IncomeSource> incomes = incomeRepository.findByDateBetween(from, to);
        return incomes.stream()
                .map(IncomeSource::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getTotalExpenses(LocalDate from, LocalDate to) {
        List<Expenditure> expenses = expenditureRepository.findByDateBetween(from, to);
        return expenses.stream()
                .map(Expenditure::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    @Override
    public BigDecimal getNetSavings(LocalDate from, LocalDate to) {
        BigDecimal income = getTotalIncome(from, to);
        BigDecimal expenses = getTotalExpenses(from, to);
        return income.subtract(expenses);
    }

    @Override
    public Map<Category, BigDecimal> getSpendingByCategory(LocalDate from, LocalDate to) {
        List<Expenditure> expenses = expenditureRepository.findByDateBetween(from, to);
        Map<Category, BigDecimal> result = new EnumMap<>(Category.class);

        for (Expenditure e : expenses) {
            Category cat = e.getCategory();
            BigDecimal current = result.getOrDefault(cat, BigDecimal.ZERO);
            result.put(cat, current.add(e.getAmount()));
        }

        return result;
    }
}