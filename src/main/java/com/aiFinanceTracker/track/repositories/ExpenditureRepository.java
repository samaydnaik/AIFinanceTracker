package com.aiFinanceTracker.track.repositories;

import com.aiFinanceTracker.track.entities.Expenditure;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenditureRepository extends JpaRepository<Expenditure, Long> {
}