package com.daniel.pfm.dtos.Transactions;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionRequestDTO {

    @NotNull
    private UUID categoryId;
    @NotBlank
    private String description;
    @NotNull
    @Positive
    private BigDecimal amount;
    @NotNull
    private LocalDate date;
    private String notes;

}
