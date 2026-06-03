package com.daniel.pfm.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "idempotency_records",
    uniqueConstraints = @UniqueConstraint(columnNames = {"idem_key", "user_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "idem_key", nullable = false)
    private String idemKey;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "response_body", nullable = false, columnDefinition = "TEXT")
    private String responseBody;

    @Column(name = "status_code", nullable = false)
    private int statusCode;

    @CreationTimestamp
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public IdempotencyRecord(String idemKey, User user, String responseBody, int statusCode) {
        this.idemKey = idemKey;
        this.user = user;
        this.responseBody = responseBody;
        this.statusCode = statusCode;
    }
}
