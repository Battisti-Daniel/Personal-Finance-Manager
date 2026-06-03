package com.daniel.pfm.models;

import com.daniel.pfm.dtos.Category.CategoryRequestDTO;
import com.daniel.pfm.enums.TransactionType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Table(name = "categories")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TransactionType type;

    @Column(length = 7, nullable = false)
    private String color;

    @Column(length = 50)
    private String icon;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Category(User user, CategoryRequestDTO entity) {
        this.user = user;
        this.name = entity.getName();
        this.type = entity.getType();
        this.color = entity.getColor();
        this.icon = entity.getIcon();
    }

    public Category(User user, String name, TransactionType type, String color, String icon) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.color = color;
        this.icon = icon;
    }
}
