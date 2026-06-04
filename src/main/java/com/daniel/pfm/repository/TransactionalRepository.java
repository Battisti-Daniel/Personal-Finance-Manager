package com.daniel.pfm.repository;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Transaction;
import com.daniel.pfm.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionalRepository extends JpaRepository<Transaction, UUID>, JpaSpecificationExecutor<Transaction> {

    Optional<Transaction> findByIdAndUserAndDeletedAtIsNull(UUID id, User user);

    @Query("""
            SELECT SUM(t.amount) FROM Transaction t
                WHERE t.user = :user AND
                    t.type = :type AND
                        t.date BETWEEN :start AND :end AND
                            t.deletedAt IS NULL
    """)
    BigDecimal sumByUserAndUserAndTypeAndDataBetween(
            @Param("user") User user,
            @Param("type")TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );

    @Query("""
            SELECT t.category, SUM(t.amount) FROM Transaction t
                WHERE t.user = :user AND
                    t.type = :type AND
                        t.date BETWEEN :start AND :end AND
                            t.deletedAt IS NULL
                                GROUP BY t.category
    """)
    List<Object[]> sumByCategoryAndUserAndTypeAndDataBetween(
            @Param("user") User user,
            @Param("type")TransactionType type,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end
    );
}
