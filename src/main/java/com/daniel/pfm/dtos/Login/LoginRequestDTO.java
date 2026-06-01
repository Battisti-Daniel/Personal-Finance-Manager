package com.daniel.pfm.dtos.Login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequestDTO {

    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String deviceId;


}
