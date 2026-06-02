package com.daniel.pfm.dtos.Error;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ValidationErrorResponseDTO {

    private final String type = "https://api.pfm.com/errors/validation";
    private final String title = "Dados inválidos";
    private int status;
    private String instance;
    private List<FieldErrorDto> errors;
    private LocalDateTime timestamp;

    public ValidationErrorResponseDTO(int status, String instance, List<FieldErrorDto> errors) {
        this.status = status;
        this.instance = instance;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
