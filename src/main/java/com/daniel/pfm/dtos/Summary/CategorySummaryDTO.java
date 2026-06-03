package com.daniel.pfm.dtos.Summary;

import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
public class CategorySummaryDTO {

    private final UUID categoryId;
    private final String categoryName;
    private final BigDecimal total;
    private final BigDecimal percentage;
    private final BigDecimal budgetAmount;
    private final BigDecimal budgetUsedPercentage;

    public CategorySummaryDTO(UUID categoryId, String categoryName, BigDecimal total,
                               BigDecimal percentage, BigDecimal budgetAmount, BigDecimal budgetUsedPercentage) {
        this.categoryId = categoryId;
        this.categoryName = categoryName;
        this.total = total;
        this.percentage = percentage;
        this.budgetAmount = budgetAmount;
        this.budgetUsedPercentage = budgetUsedPercentage;
    }
}
