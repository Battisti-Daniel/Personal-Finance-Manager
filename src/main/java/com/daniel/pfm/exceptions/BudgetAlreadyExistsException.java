package com.daniel.pfm.exceptions;

public class BudgetAlreadyExistsException extends RuntimeException {
    public BudgetAlreadyExistsException() {
        super("Já existe um orçamento para essa categoria nesse mês");
    }
}
