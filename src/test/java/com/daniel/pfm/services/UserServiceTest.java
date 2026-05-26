package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Auth.AuthResponseDTO;
import com.daniel.pfm.dtos.Login.LoginRequestDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.dtos.User.UserResponseDTO;
import com.daniel.pfm.exceptions.UserAlreadyExistsException;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UserService userService;

    @Test
    void shouldRegisterUserSuccessfully(){

        UserRequestDTO request = new UserRequestDTO(
                "daniel@email.com",
                "123456789",
                "daniel"
        );

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(repository.save(any(User.class))).thenAnswer(invocation -> (User) invocation.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        AuthResponseDTO response = userService.register(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("daniel@email.com", response.getUser().getEmail());

    }

    @Test
    void shouldThrowExceptionEmailAlreadyExists(){

        UserRequestDTO request = new UserRequestDTO(
                "daniel@email.com",
                "123456789",
                "Daniel"
        );

        when(repository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> userService.register(request));

        verify(repository, never()).save(any());

    }

    @Test
    void shouldHashPasswordBeforeSaving(){

        UserRequestDTO request = new UserRequestDTO(
                "daniel@email.com",
                "123456789",
                "Daniel"
        );

        when(repository.existsByEmail(request.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(request.getPassword())).thenReturn("hashedPassword");
        when(repository.save(any(User.class))).thenAnswer(invocation -> (User) invocation.getArgument(0));
        when(jwtService.generateToken(any())).thenReturn("token");

        userService.register(request);

        verify(passwordEncoder).encode(request.getPassword());
        verify(repository).save(argThat(user -> user.getPassword().equals("hashedPassword")));


    }

    @Test
    void shouldLoginSuccessfully(){

        LoginRequestDTO request = new LoginRequestDTO(
          "daniel@email.com",
          "123456789"
        );

        User user = new User(new UserRequestDTO
                (
                        "daniel@email.com",
                        "hashedPassword",
                        "Daniel"
                ),
                "hashedPassword"
        );

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user.getEmail())).thenReturn("token");

        AuthResponseDTO response = userService.login(request);

        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("daniel@email.com", response.getUser().getEmail());

        }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnLogin() {

        LoginRequestDTO request = new LoginRequestDTO(
                "daniel@email.com",
                "123456789"
        );

        when(authenticationManager.authenticate(any())).thenThrow(new UsernameNotFoundException("Credenciais inválidas"));
        assertThrows(UsernameNotFoundException.class, () -> userService.login(request));

        verify(repository, never()).findByEmail(any());

    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundAfterAuthentication(){

        LoginRequestDTO request = new LoginRequestDTO(
                "daniel@email.com",
                "senha123"
        );

        when(repository.findByEmail(request.getEmail())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.login(request));

    }




    }
