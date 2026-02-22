package com.aiFinanceTracker.track.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.aiFinanceTracker.track.entities.Expenditure;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {


    // if method name doesn’t work reliably, use @Query:
    @Query("select coalesce(sum(e.amount), 0) from Expenditure e " +
           "where e.date between :start and :end")
    BigDecimal totalSpentBetween(@Param("start") LocalDate start,
                                 @Param("end") LocalDate end);
    
    List<Expenditure> findByDateBetween(LocalDate start, LocalDate end);

}
