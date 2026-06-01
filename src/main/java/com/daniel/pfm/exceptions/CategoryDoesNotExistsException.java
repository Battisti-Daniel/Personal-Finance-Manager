package com.daniel.pfm.exceptions;

public class CategoryDoesNotExistsException extends RuntimeException {
    public CategoryDoesNotExistsException() {
        super("Categoria não encontrada");
    }
}
