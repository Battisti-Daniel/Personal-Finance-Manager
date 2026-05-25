package com.daniel.pfm.dtos.Auth;

import com.daniel.pfm.dtos.User.UserResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthResponseDTO {

    private String token;
    private UserResponseDTO user;

}
