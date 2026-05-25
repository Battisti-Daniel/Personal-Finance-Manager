package com.daniel.pfm.controllers;

import com.daniel.pfm.dtos.Auth.AuthResponseDTO;
import com.daniel.pfm.dtos.Login.LoginRequestDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.dtos.User.UserResponseDTO;
import com.daniel.pfm.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO entity) {

        AuthResponseDTO response = userService.register(entity);

        URI uri = ServletUriComponentsBuilder.fromCurrentRequestUri()
                .path("/{id}")
                .buildAndExpand(response.getUser().getId())
                .toUri();

        return ResponseEntity.created(uri).body(response);

    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO){

        AuthResponseDTO response = userService.login(loginRequestDTO);

        return ResponseEntity.ok().body(response);

    }


}
