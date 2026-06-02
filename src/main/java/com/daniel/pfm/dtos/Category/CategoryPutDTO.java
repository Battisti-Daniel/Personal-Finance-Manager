package com.daniel.pfm.dtos.Category;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Category;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CategoryPutDTO {

    @Size(min = 2, max = 50)
    private String name;
    private TransactionType type;
    @Size(max = 7)
    private String color;
    @Size(max = 50)
    private String icon;

    public CategoryPutDTO(Category entity) {
        this.name = entity.getName();
        this.type = entity.getType();
        this.color = entity.getColor();
        this.icon = entity.getIcon();
    }

    public boolean isEmpty(){
        return name == null && type == null && color == null && icon == null;
    }
}
