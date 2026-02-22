package com.aiFinanceTracker.track.repositories;

import com.aiFinanceTracker.track.entities.Savings;

import java.math.BigDecimal;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SavingsRepository extends JpaRepository<Savings, Long> {
	@Query("select coalesce(sum(s.amount), 0) from Savings s")
	BigDecimal totalSavings();

}
