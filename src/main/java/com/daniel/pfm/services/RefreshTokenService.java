package com.daniel.pfm.services;

import com.daniel.pfm.exceptions.RefreshTokenExpiredException;
import com.daniel.pfm.exceptions.RefreshTokenNotFoundException;
import com.daniel.pfm.models.RefreshToken;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository repository;

    @Value("${jwt.refresh-expiration}")
    private Long refreshExpiration;

    public RefreshToken createRefreshToken(User user, String deviceId){

        repository.findByUserAndDeviceId(user, deviceId).ifPresent(repository::delete);

        RefreshToken refreshToken = new RefreshToken(
                user,
                UUID.randomUUID().toString(),
                deviceId,
                LocalDateTime.now().plusSeconds(refreshExpiration / 1000 )
        );

        return repository.save(refreshToken);

    }

    public RefreshToken validateRefreshToken(String token){

        RefreshToken refreshToken = repository.findByToken(token).orElseThrow(
                RefreshTokenNotFoundException::new
        );

        if(refreshToken.isExpired()){
            repository.delete(refreshToken);
            throw new RefreshTokenExpiredException();
        }

        return refreshToken;

    }
}
