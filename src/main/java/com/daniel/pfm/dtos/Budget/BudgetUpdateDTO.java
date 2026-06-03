package com.daniel.pfm.dtos.Budget;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@NoArgsConstructor
public class BudgetUpdateDTO {

    @NotNull
    @Positive
    private BigDecimal amount;
}
