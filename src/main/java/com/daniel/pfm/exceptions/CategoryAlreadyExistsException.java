package com.daniel.pfm.exceptions;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException() {
        super("Categoria já existe para esse tipo de transação");
    }
}
