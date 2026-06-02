package com.daniel.pfm.exceptions;

public class TransactionalNotFoundException extends RuntimeException {
    public TransactionalNotFoundException() {
        super("Transação não encontrada");
    }
}
