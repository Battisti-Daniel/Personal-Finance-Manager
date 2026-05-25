package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Auth.AuthResponseDTO;
import com.daniel.pfm.dtos.Login.LoginRequestDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.dtos.User.UserResponseDTO;
import com.daniel.pfm.exceptions.UserAlreadyExistsException;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(UserRequestDTO entity){

        if(repository.existsByEmail(entity.getEmail())){
            throw new UserAlreadyExistsException(entity.getEmail());
        }

        User user = createUser(entity);

        repository.save(user);

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(token, new UserResponseDTO(user));

    }

    public AuthResponseDTO login(LoginRequestDTO entity){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        entity.getEmail(),
                        entity.getPassword()
                ));

        User user = repository.findByEmail(entity.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("Usuario não encontrado")
        );

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(token, new UserResponseDTO(user));

    }

    private User createUser(UserRequestDTO userDTO){

        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        return new User(userDTO, hashedPassword);

    }


}
