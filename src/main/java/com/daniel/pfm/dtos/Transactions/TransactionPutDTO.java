package com.daniel.pfm.dtos.Transactions;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
public class TransactionPutDTO {

    private String description;
    private BigDecimal amount;
    private String notes;
    private LocalDate date;

    public boolean isEmpty(){
        return description == null && amount == null &&  notes == null && date == null;
    }

}
