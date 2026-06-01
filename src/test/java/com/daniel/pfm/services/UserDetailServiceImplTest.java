package com.daniel.pfm.services;

import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserDetailServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDetailServiceImpl userDetailService;

    @Test
    void shouldLoadUserByUsername() {

        String email = "daniel@email.com";

        User user = new User(new UserRequestDTO
                (
                        "daniel@email.com",
                        "hashedPassword",
                        "daniel",
                        "Daniel"
                ),
                "hashedPassword"
        );

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        var result = userDetailService.loadUserByUsername(email);

        assertNotNull(result);
        assertEquals(email, result.getUsername());

    }

    @Test
    void shouldThrowUsernameNotFoundExceptionWhenNotValidEmail(){

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userDetailService.loadUserByUsername("invalidEmail"));


    }

}
