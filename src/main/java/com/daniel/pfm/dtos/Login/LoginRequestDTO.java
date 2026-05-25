package com.daniel.pfm.dtos.Login;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    private String email;
    private String password;

}
