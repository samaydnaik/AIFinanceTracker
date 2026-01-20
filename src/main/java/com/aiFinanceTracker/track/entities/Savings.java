package com.aiFinanceTracker.track.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aiFinanceTracker.track.enums.SavingsType;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "savings")
public class Savings {
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING) 
    private SavingsType type;  // FD, MF, etc.
    private LocalDate date = LocalDate.now();
    private LocalDate maturityDate;
    private BigDecimal interestRate;
    // getters/setters
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public SavingsType getType() {
		return type;
	}
	public void setType(SavingsType type) {
		this.type = type;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public LocalDate getMaturityDate() {
		return maturityDate;
	}
	public void setMaturityDate(LocalDate maturityDate) {
		this.maturityDate = maturityDate;
	}
	public BigDecimal getInterestRate() {
		return interestRate;
	}
	public void setInterestRate(BigDecimal interestRate) {
		this.interestRate = interestRate;
	}
}
