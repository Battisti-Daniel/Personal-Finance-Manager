package com.daniel.pfm.repository;

import com.daniel.pfm.models.Budget;
import com.daniel.pfm.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BudgetRepository extends JpaRepository<Budget, UUID> {

    Optional<Budget> findByCategoryAndYearMonth(Category category, String yearMonth);

    Optional<Budget> findByIdAndCategory_User_Email(UUID id, String email);

    List<Budget> findAllByCategory_User_EmailAndYearMonth(String email, String yearMonth);

    boolean existsByCategoryAndYearMonth(Category category, String yearMonth);
}
