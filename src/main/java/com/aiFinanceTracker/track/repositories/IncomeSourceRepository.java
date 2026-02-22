package com.aiFinanceTracker.track.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aiFinanceTracker.track.entities.IncomeSource;

public interface IncomeSourceRepository extends JpaRepository<IncomeSource, Long>{
	@Query("select coalesce(sum(i.amount), 0) from IncomeSource i " +
		       "where i.startDate <= :end")
		BigDecimal totalIncomeUpTo(@Param("end") LocalDate end);

}
