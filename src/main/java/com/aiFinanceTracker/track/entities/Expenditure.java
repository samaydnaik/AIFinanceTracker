package com.aiFinanceTracker.track.entities;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.aiFinanceTracker.track.enums.Category;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity @Table(name = "expenditures")
public class Expenditure {
	@Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY) 
    private Long id;
    private BigDecimal amount;
    @Enumerated(EnumType.STRING) 
    private Category category;
    private LocalDate date = LocalDate.now();
    private String merchant;
    private boolean recurring;
    // enum Category { FOOD, RENT, TRANSPORT, ... }
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
	public Category getCategory() {
		return category;
	}
	public void setCategory(Category category) {
		this.category = category;
	}
	public LocalDate getDate() {
		return date;
	}
	public void setDate(LocalDate date) {
		this.date = date;
	}
	public String getMerchant() {
		return merchant;
	}
	public void setMerchant(String merchant) {
		this.merchant = merchant;
	}
	public boolean isRecurring() {
		return recurring;
	}
	public void setRecurring(boolean recurring) {
		this.recurring = recurring;
	}
}
