package com.daniel.pfm.dtos.Summary;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class SummaryResponseDTO {

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal balance;
    private String month;

    public SummaryResponseDTO(BigDecimal totalIncome, BigDecimal totalExpense, String month) {
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        this.balance = totalIncome.subtract(totalExpense);
        this.month = month;
    }
}
