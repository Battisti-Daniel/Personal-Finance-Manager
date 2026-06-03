package com.daniel.pfm.exceptions;

public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException() {
        super("Orçamento não encontrado");
    }
}
