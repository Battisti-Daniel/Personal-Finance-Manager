package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Summary.CategorySummaryDTO;
import com.daniel.pfm.dtos.Summary.SummaryResponseDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Budget;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.BudgetRepository;
import com.daniel.pfm.repository.TransactionalRepository;
import com.daniel.pfm.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final UserRepository userRepository;
    private final TransactionalRepository repository;
    private final BudgetRepository budgetRepository;

    public SummaryResponseDTO getMonthlySummary(String username, String month) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        BigDecimal totalIncome = repository.sumByUserAndUserAndTypeAndDataBetween(user, TransactionType.INCOME, start, end);
        BigDecimal totalExpense = repository.sumByUserAndUserAndTypeAndDataBetween(user, TransactionType.EXPENSE, start, end);

        totalIncome = totalIncome != null ? totalIncome : BigDecimal.ZERO;
        totalExpense = totalExpense != null ? totalExpense : BigDecimal.ZERO;

        return new SummaryResponseDTO(totalIncome, totalExpense, month);
    }

    public List<CategorySummaryDTO> getCategorySummary(String username, String month, TransactionType type) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        YearMonth ym = YearMonth.parse(month);
        LocalDate start = ym.atDay(1);
        LocalDate end = ym.atEndOfMonth();

        List<Object[]> results = repository.sumByCategoryAndUserAndTypeAndDataBetween(user, type, start, end);

        BigDecimal totalGeral = results.stream()
                .map(row -> (BigDecimal) row[1])
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return results.stream()
                .map(row -> {
                    Category category = (Category) row[0];
                    BigDecimal total = (BigDecimal) row[1];

                    BigDecimal percentage = totalGeral.compareTo(BigDecimal.ZERO) == 0
                            ? BigDecimal.ZERO
                            : total.divide(totalGeral, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));

                    Optional<Budget> budget = budgetRepository.findByCategoryAndYearMonth(category, month);
                    BigDecimal budgetAmount = budget.map(Budget::getAmount).orElse(null);
                    BigDecimal budgetUsedPercentage = (budgetAmount != null && budgetAmount.compareTo(BigDecimal.ZERO) > 0)
                            ? total.divide(budgetAmount, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100))
                            : null;

                    return new CategorySummaryDTO(
                            category.getId(), category.getName(),
                            total, percentage,
                            budgetAmount, budgetUsedPercentage
                    );
                }).toList();
    }
}
