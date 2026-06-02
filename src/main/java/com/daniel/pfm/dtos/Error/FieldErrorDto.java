package com.daniel.pfm.dtos.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldErrorDto {

    private String field;
    private String message;

}
