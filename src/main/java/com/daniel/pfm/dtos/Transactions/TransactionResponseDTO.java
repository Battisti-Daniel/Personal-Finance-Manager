package com.daniel.pfm.dtos.Transactions;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Transaction;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class TransactionResponseDTO {

    private UUID id;
    private UUID categoryId;
    private String categoryName;
    private String description;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate date;
    private String notes;
    private LocalDateTime createdAt;

     public TransactionResponseDTO(Transaction entity) {
        this.id = entity.getId();
        this.categoryId = entity.getCategory().getId();
        this.categoryName = entity.getCategory().getName();
        this.description = entity.getDescription();
        this.amount = entity.getAmount();
        this.type = entity.getType();
        this.date = entity.getDate();
        this.notes = entity.getNotes();
        this.createdAt = entity.getCreatedAt();
    }

}
