package com.daniel.pfm.repository;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByIdAndUser(UUID id, User user);

    boolean existsByNameAndTypeAndUser(String name, TransactionType type, User user);

    Optional<Category> findByIdAndUser(UUID id, User user);

    List<Category> findAllByUser(User user);

}
