package com.daniel.pfm.dtos.Budget;

import com.daniel.pfm.models.Budget;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class BudgetResponseDTO {

    private final UUID id;
    private final UUID categoryId;
    private final String categoryName;
    private final String yearMonth;
    private final BigDecimal amount;

    public BudgetResponseDTO(Budget budget) {
        this.id = budget.getId();
        this.categoryId = budget.getCategory().getId();
        this.categoryName = budget.getCategory().getName();
        this.yearMonth = budget.getYearMonth();
        this.amount = budget.getAmount();
    }
}
