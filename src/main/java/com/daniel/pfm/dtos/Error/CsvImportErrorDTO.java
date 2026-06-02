package com.daniel.pfm.dtos.Error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CsvImportErrorDTO {

    private int line;
    private String message;
}
