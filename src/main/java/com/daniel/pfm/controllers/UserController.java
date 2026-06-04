package com.daniel.pfm.controllers;

import com.daniel.pfm.dtos.Auth.AuthResponseDTO;
import com.daniel.pfm.dtos.Auth.RefreshRequestDTO;
import com.daniel.pfm.dtos.Login.LoginRequestDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.dtos.User.UserResponseDTO;
import com.daniel.pfm.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/api/v1/auth/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO entity) {

        AuthResponseDTO response = userService.register(entity);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/api/v1/auth/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO){

        AuthResponseDTO response = userService.login(loginRequestDTO);

        return ResponseEntity.ok().body(response);

    }

    @PostMapping("/api/v1/auth/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@Valid @RequestBody RefreshRequestDTO entity){

        AuthResponseDTO response = userService.refresh(entity);

        return ResponseEntity.ok().body(response);

    }

    @GetMapping("/api/v1/users/me")
    public ResponseEntity<UserResponseDTO> me(Authentication authentication){

        UserResponseDTO response = userService.me(authentication.getName());

        return ResponseEntity.ok().body(response);

    }

}
