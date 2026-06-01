package com.daniel.pfm.dtos.Transactions;

import com.daniel.pfm.models.User;
import jakarta.validation.constraints.NotNull;
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
    @NotNull
    private User user;
    @NotNull
    private String description;
    @NotNull
    private BigDecimal amount;
    @NotNull
    private LocalDate date;
    private String notes;

}
