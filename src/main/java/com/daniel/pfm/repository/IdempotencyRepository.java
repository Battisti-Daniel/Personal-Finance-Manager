package com.daniel.pfm.repository;

import com.daniel.pfm.models.IdempotencyRecord;
import com.daniel.pfm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface IdempotencyRepository extends JpaRepository<IdempotencyRecord, UUID> {

    Optional<IdempotencyRecord> findByIdemKeyAndUser(String idemKey, User user);

    @Modifying
    @Query("UPDATE IdempotencyRecord r SET r.responseBody = :body, r.statusCode = :status WHERE r.idemKey = :key AND r.user = :user")
    void updateResponse(@Param("key") String idemKey, @Param("user") User user,
                        @Param("body") String responseBody, @Param("status") int statusCode);
}
