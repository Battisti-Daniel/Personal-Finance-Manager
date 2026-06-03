package com.daniel.pfm.dtos.Budget;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BudgetRequestDTO {

    @NotNull
    private UUID categoryId;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}", message = "yearMonth deve estar no formato YYYY-MM")
    private String yearMonth;

    @NotNull
    @Positive
    private BigDecimal amount;
}
