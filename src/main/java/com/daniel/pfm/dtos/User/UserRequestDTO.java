package com.daniel.pfm.dtos.User;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserRequestDTO {
    @Email
    private String email;
    @Size(min = 3, max = 255, message = "A senha deve conter no minimo 3 caracteres")
    private String password;
    @Size(min = 3, max = 100, message = "O nome deve conter no minimo 3 caracteres")
    private String name;
    @NotBlank
    private String deviceId;

}
