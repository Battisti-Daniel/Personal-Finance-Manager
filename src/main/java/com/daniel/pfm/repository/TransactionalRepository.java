package com.daniel.pfm.repository;

import com.daniel.pfm.models.Transaction;
import com.daniel.pfm.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;
import java.util.UUID;

public interface TransactionalRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUser(UUID id, User user);

    Page<Transaction> findAllByUser(User user, Pageable pageable);
}
