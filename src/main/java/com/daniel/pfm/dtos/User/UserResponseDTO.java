package com.daniel.pfm.dtos.User;

import com.daniel.pfm.models.User;
import lombok.Getter;

import java.util.UUID;

public class UserResponseDTO {
    @Getter
    private UUID id;
    @Getter
    private String email;
    private String name;

    public UserResponseDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.name = user.getName();
    }

}