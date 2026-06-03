package com.daniel.pfm.dtos.Summary;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@AllArgsConstructor
public class CategorySummaryDTO {

    private UUID categoryId;
    private String categoryName;
    private BigDecimal total;
    private BigDecimal percentage;

}
