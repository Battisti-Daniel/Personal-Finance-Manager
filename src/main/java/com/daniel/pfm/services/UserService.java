package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Auth.AuthResponseDTO;
import com.daniel.pfm.dtos.Auth.RefreshRequestDTO;
import com.daniel.pfm.dtos.Login.LoginRequestDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.dtos.User.UserResponseDTO;
import com.daniel.pfm.exceptions.UserAlreadyExistsException;
import com.daniel.pfm.models.RefreshToken;
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
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;

    @Transactional
    public AuthResponseDTO register(UserRequestDTO entity){

        if(repository.existsByEmail(entity.getEmail())){
            throw new UserAlreadyExistsException(entity.getEmail());
        }

        User user = createUser(entity);

        repository.save(user);

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, entity.getDeviceId());

        return new AuthResponseDTO(accessToken, refreshToken.getToken(), new UserResponseDTO(user));

    }

    @Transactional
    public AuthResponseDTO login(LoginRequestDTO entity){

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        entity.getEmail(),
                        entity.getPassword()
                ));

        User user = repository.findByEmail(entity.getEmail()).orElseThrow(
                () -> new UsernameNotFoundException("Usuario não encontrado")
        );

        String accessToken = jwtService.generateToken(user.getEmail());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user, entity.getDeviceId());

        return new AuthResponseDTO(accessToken, refreshToken.getToken(), new UserResponseDTO(user));

    }

    @Transactional
    public AuthResponseDTO refresh(RefreshRequestDTO entity){

        RefreshToken refreshToken = refreshTokenService.validateRefreshToken(entity.getRefreshToken());

        User user = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(user.getEmail());

        return new AuthResponseDTO(newAccessToken, refreshToken.getToken(), new UserResponseDTO(user));

    }

    private User createUser(UserRequestDTO userDTO){

        String hashedPassword = passwordEncoder.encode(userDTO.getPassword());

        return new User(userDTO, hashedPassword);

    }

}
