package com.daniel.pfm.models;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Table(name = "budget")
@Entity
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(mappedBy = "budget")
    private User user;

    @OneToMany(mappedBy = "budget", cascade = CascadeType.ALL)
    private List<Category> categories;

    @Column(nullable = false, precision = 2, length = 15)
    private BigDecimal amount;

    @Column(unique = true)
    private Date year_month;

    @CreationTimestamp
    @Column(nullable = false)
    private LocalDateTime created_at;

    @UpdateTimestamp
    private LocalDateTime updated_at;

}
