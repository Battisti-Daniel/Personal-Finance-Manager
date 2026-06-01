package com.daniel.pfm.repository;

import com.daniel.pfm.models.RefreshToken;
import com.daniel.pfm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);
    Optional<RefreshToken> findByUserAndDeviceId(User user, String deviceId);

    void deleteByUser(User user);

}
