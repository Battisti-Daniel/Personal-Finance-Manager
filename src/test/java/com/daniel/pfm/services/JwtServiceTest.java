package com.daniel.pfm.services;

import com.daniel.pfm.models.RefreshToken;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp(){

        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", "mysecretkeymysecretkeymysecretkeymy");
        ReflectionTestUtils.setField(jwtService,"expiration", 86400000L);

    }

    @Test
    void shouldGeneratedValidToken(){

        String token = jwtService.generateToken("daniel@email.com");

        assertNotNull(token);
        assertFalse(token.isEmpty());

    }

    @Test
    void shouldExtractEmailFromToken(){

        String email = "daniel@email.com";
        String token = jwtService.generateToken(email);

        assertEquals(email, jwtService.extractEmail(token));

    }

    @Test
    void shouldReturnTrueForValidToken(){

        String token = jwtService.generateToken("daniel@email.com");

        assertTrue(jwtService.isTokenValid(token));

    }

    @Test
    void shouldReturnFalseForInvalidToken(){

        assertFalse(jwtService.isTokenValid("token.invalido"));

    }

    @Test
    void shouldReturnFalseForExpiredToken(){

        ReflectionTestUtils.setField(jwtService, "expiration", -1000L);

        String token = jwtService.generateToken("daniel@email.com");

        assertFalse(jwtService.isTokenValid(token));

    }


}
