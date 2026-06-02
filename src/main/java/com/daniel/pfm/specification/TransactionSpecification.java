package com.daniel.pfm.specification;

import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Transaction;
import com.daniel.pfm.models.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.UUID;

public class TransactionSpecification {

    public static Specification<Transaction> byUser(User user){

        return (root, query, cb) -> cb.equal(root.get("user"), user);

    }

    public static Specification<Transaction> byType(TransactionType type){
        return (root, query, cb) -> type == null ? null : cb.equal(root.get("type"), type);
    }

    public static Specification<Transaction> byCategoryId(UUID categoryId){
        return (root, query, cb) -> categoryId == null ? null : cb.equal(root.get("category").get("id"),categoryId);
    }

    public static Specification<Transaction> byMonth(String month){

        return (root, query, cb) -> {

            if(month == null){
                return null;
            }

            YearMonth yearMonth = YearMonth.parse(month);
            LocalDate start = yearMonth.atDay(1);
            LocalDate end = yearMonth.atEndOfMonth();

            return cb.between(root.get("date"), start, end);

        };

    }



}
