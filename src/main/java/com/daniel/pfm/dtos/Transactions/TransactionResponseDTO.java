package com.daniel.pfm.dtos.Transactions;

import com.daniel.pfm.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransactionResponseDTO {

    private UUID id;
    private UUID categoryId;
    private String description;
    private BigDecimal amount;
    private LocalDate date;
    private String notes;

     public TransactionResponseDTO(Transaction entity) {
        this.id = entity.getId();
        this.categoryId = entity.getCategory().getId();
        this.description = entity.getDescription();
        this.amount = entity.getAmount();
        this.date = entity.getDate();
        this.notes = entity.getNotes();
    }

}
