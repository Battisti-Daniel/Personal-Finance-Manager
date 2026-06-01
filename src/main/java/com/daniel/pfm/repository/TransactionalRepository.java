package com.daniel.pfm.repository;

import com.daniel.pfm.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionalRepository extends JpaRepository<Transaction, UUID> {
}
