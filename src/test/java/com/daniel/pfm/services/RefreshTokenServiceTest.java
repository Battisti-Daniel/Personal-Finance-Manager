package com.daniel.pfm.services;

import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.exceptions.RefreshTokenExpiredException;
import com.daniel.pfm.exceptions.RefreshTokenNotFoundException;
import com.daniel.pfm.models.RefreshToken;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.RefreshTokenRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @InjectMocks
    private RefreshTokenService refreshTokenService;

    private User user;

    @BeforeEach
    void setUp(){

        ReflectionTestUtils.setField(refreshTokenService, "refreshExpiration", 604800000L);

        user = new User(
                new UserRequestDTO("daniel@email.com", "senha123", "Daniel", "device-1"),
                "hashedPassword"
        );

    }

    @Test
    void shouldCreateRefreshTokenSuccessfully(){

        when(refreshTokenRepository.findByUserAndDeviceId(user, "device-1")).thenReturn(Optional.empty());
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> (RefreshToken) invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(user, "device-1");

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals("device-1", result.getDeviceId());
        verify(refreshTokenRepository, never()).delete(any());

    }

    @Test
    void shouldReplaceExistingTokenFormSameDevice(){

        RefreshToken existing = new RefreshToken(user,"token-antigo", "device-1", LocalDateTime.now().plusDays(7));

        when(refreshTokenRepository.findByUserAndDeviceId(user,"device-1")).thenReturn(Optional.of(existing));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenAnswer(invocation -> (RefreshToken) invocation.getArgument(0));

        RefreshToken result = refreshTokenService.createRefreshToken(user, "device-1");

        verify(refreshTokenRepository).delete(existing);
        assertNotNull(result.getToken());
        assertNotEquals("token-antigo", result.getToken());

    }

    @Test
    void shouldValidateValidToken(){

        RefreshToken refreshToken = new RefreshToken(user,"token-valido", "device-1", LocalDateTime.now().plusDays(7));

        when(refreshTokenRepository.findByToken("token-valido")).thenReturn(Optional.of(refreshToken));

        RefreshToken result = refreshTokenService.validateRefreshToken("token-valido");

        assertNotNull(result);
        assertEquals("token-valido", result.getToken());

    }

    @Test
    void shouldThrowExceptionWhenTokenNotFound() {
        when(refreshTokenRepository.findByToken(any())).thenReturn(Optional.empty());

        assertThrows(RefreshTokenNotFoundException.class,
                () -> refreshTokenService.validateRefreshToken("token-inexistente"));
    }

    @Test
    void shouldThrowExceptionAndDeleteWhenTokenExpired() {
        RefreshToken expired = new RefreshToken(user, "token-expirado", "device-1", LocalDateTime.now().minusDays(1));

        when(refreshTokenRepository.findByToken("token-expirado")).thenReturn(Optional.of(expired));

        assertThrows(RefreshTokenExpiredException.class,
                () -> refreshTokenService.validateRefreshToken("token-expirado"));

        verify(refreshTokenRepository).delete(expired);
    }




}
