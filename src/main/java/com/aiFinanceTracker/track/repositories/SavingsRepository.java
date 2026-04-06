package com.aiFinanceTracker.track.repositories;

import com.aiFinanceTracker.track.entities.Savings;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SavingsRepository extends JpaRepository<Savings, Long> {
	@Query("select coalesce(sum(s.amount), 0) from Savings s")
	BigDecimal totalSavings();
	
	List<Savings> findByDateBetween(LocalDate from, LocalDate to);

}
