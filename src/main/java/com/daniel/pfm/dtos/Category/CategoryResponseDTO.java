package com.daniel.pfm.dtos.Category;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Category;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public class CategoryResponseDTO {

    private final UUID id;
    private final String name;
    private final TransactionType type;
    private final String color;
    private final String icon;
    private final LocalDateTime createdAt;

    public CategoryResponseDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
        this.type = entity.getType();
        this.color = entity.getColor();
        this.icon = entity.getIcon();
        this.createdAt = entity.getCreatedAt();
    }
}
