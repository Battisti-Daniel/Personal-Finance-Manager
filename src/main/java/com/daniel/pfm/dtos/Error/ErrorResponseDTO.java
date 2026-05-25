package com.daniel.pfm.dtos.Error;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ErrorResponseDTO {

    private int status;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ErrorResponseDTO(int status, List<String> errors) {
        this.status = status;
        this.errors = errors;
        this.timestamp = LocalDateTime.now();
    }
}
