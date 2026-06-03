package com.daniel.pfm.dtos.Transactions;

import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class TransactionPutDTO {

    private String description;
    @Positive
    private BigDecimal amount;
    private String notes;
    @PastOrPresent(message = "A data não pode ser futura")
    private LocalDate date;

    public boolean isEmpty(){
        return description == null && amount == null &&  notes == null && date == null;
    }

}
