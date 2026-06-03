package com.daniel.pfm.dtos.csv;

import com.daniel.pfm.dtos.Error.CsvImportErrorDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class CSVImportResponseDTO {

    private int imported;
    private int autoCategorized;
    private int rejected;
    private List<CsvImportErrorDTO> errors;

}
