package com.daniel.pfm.dtos.Category;

import com.daniel.pfm.enums.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

@Getter
@Setter
public class CategoryRequestDTO {

    @NotBlank
    @Length(min = 2, max = 50)
    private String name;
    @NotNull
    private TransactionType type;
    @NotBlank
    @Size(max = 7)
    private String color;
    @NotBlank
    @Size(max = 50)
    private String icon;

}
