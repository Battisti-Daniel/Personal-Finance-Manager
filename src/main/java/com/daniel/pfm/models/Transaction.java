package com.daniel.pfm.models;

import com.daniel.pfm.dtos.Transactions.TransactionRequestDTO;
import com.daniel.pfm.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "transactions")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(nullable = false, length = 200)
    private String description;

    @Column(nullable = false, scale = 2, length = 15)
    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(nullable = false)
    private LocalDate date;

    private  String notes;

    private LocalDateTime deletedAt;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    public Transaction(TransactionRequestDTO entity, User user, Category category) {
        this.notes = entity.getNotes();
        this.date = entity.getDate();
        this.amount = entity.getAmount();
        this.description = entity.getDescription();
        this.category = category;
        this.user = user;
        this.type = category.getType();
    }
}
