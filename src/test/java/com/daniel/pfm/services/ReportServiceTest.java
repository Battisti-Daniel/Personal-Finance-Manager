package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Category.CategoryRequestDTO;
import com.daniel.pfm.dtos.Summary.CategorySummaryDTO;
import com.daniel.pfm.dtos.Summary.SummaryResponseDTO;
import com.daniel.pfm.dtos.User.UserRequestDTO;
import com.daniel.pfm.enums.TransactionType;
import com.daniel.pfm.models.Budget;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.BudgetRepository;
import com.daniel.pfm.repository.TransactionalRepository;
import com.daniel.pfm.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService service;

    @Mock
    private UserRepository userRepository;

    @Mock
    private TransactionalRepository transactionalRepository;

    @Mock
    private BudgetRepository budgetRepository;

    private User user;
    private Category category;
    private final String month = "2026-05";

    @BeforeEach
    void setUp() {
        user = new User(new UserRequestDTO(
                "daniel@email.com", "senha123", "Daniel", "device-1"
        ), "hashed");

        category = new Category(user, new CategoryRequestDTO(
                "Alimentação", TransactionType.EXPENSE, "#FF0000", "food"
        ));
        category.setId(UUID.randomUUID());
    }

    // ==================== getMonthlySummary ====================

    @Test
    void shouldReturnMonthlySummaryWithIncomeAndExpense() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionalRepository.sumByUserAndUserAndTypeAndDataBetween(
                eq(user), eq(TransactionType.INCOME), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(3000));
        when(transactionalRepository.sumByUserAndUserAndTypeAndDataBetween(
                eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(BigDecimal.valueOf(1200));

        SummaryResponseDTO result = service.getMonthlySummary(user.getEmail(), month);

        assertNotNull(result);
        assertEquals(BigDecimal.valueOf(3000), result.getTotalIncome());
        assertEquals(BigDecimal.valueOf(1200), result.getTotalExpense());
        assertEquals(BigDecimal.valueOf(1800), result.getBalance());
        assertEquals(month, result.getMonth());
    }

    @Test
    void shouldReturnZeroWhenNoTransactionsInMonth() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionalRepository.sumByUserAndUserAndTypeAndDataBetween(
                any(), any(), any(), any())).thenReturn(null);

        SummaryResponseDTO result = service.getMonthlySummary(user.getEmail(), month);

        assertEquals(BigDecimal.ZERO, result.getTotalIncome());
        assertEquals(BigDecimal.ZERO, result.getTotalExpense());
        assertEquals(BigDecimal.ZERO, result.getBalance());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnSummary() {
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                service.getMonthlySummary("inexistente@email.com", month)
        );
    }

    // ==================== getCategorySummary ====================

    @Test
    void shouldReturnCategorySummaryWithoutBudget() {
        Object[] row = new Object[]{category, BigDecimal.valueOf(500)};
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionalRepository.sumByCategoryAndUserAndTypeAndDataBetween(
                eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.<Object[]>of(row));
        when(budgetRepository.findByCategoryAndYearMonth(category, month)).thenReturn(Optional.empty());

        List<CategorySummaryDTO> result = service.getCategorySummary(user.getEmail(), month, TransactionType.EXPENSE);

        assertEquals(1, result.size());
        CategorySummaryDTO dto = result.get(0);
        assertEquals(category.getId(), dto.getCategoryId());
        assertEquals(BigDecimal.valueOf(500), dto.getTotal());
        // único item = 100%
        assertEquals(0, dto.getPercentage().compareTo(BigDecimal.valueOf(100)));
        assertNull(dto.getBudgetAmount());
        assertNull(dto.getBudgetUsedPercentage());
    }

    @Test
    void shouldReturnCategorySummaryWithBudgetAndPercentage() {
        Object[] row = new Object[]{category, BigDecimal.valueOf(600)};
        Budget budget = new Budget(null, category, month, BigDecimal.valueOf(1000), null, null);

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionalRepository.sumByCategoryAndUserAndTypeAndDataBetween(
                eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.<Object[]>of(row));
        when(budgetRepository.findByCategoryAndYearMonth(category, month)).thenReturn(Optional.of(budget));

        List<CategorySummaryDTO> result = service.getCategorySummary(user.getEmail(), month, TransactionType.EXPENSE);

        assertEquals(1, result.size());
        CategorySummaryDTO dto = result.get(0);
        assertEquals(BigDecimal.valueOf(1000), dto.getBudgetAmount());
        // 600 / 1000 * 100 = 60%
        assertEquals(0, dto.getBudgetUsedPercentage().compareTo(BigDecimal.valueOf(60)));
    }

    @Test
    void shouldReturnPercentageCorrectlyWithMultipleCategories() {
        Category category2 = new Category(user, new CategoryRequestDTO(
                "Transporte", TransactionType.EXPENSE, "#00FF00", "car"
        ));
        category2.setId(UUID.randomUUID());

        Object[] row1 = new Object[]{category, BigDecimal.valueOf(300)};
        Object[] row2 = new Object[]{category2, BigDecimal.valueOf(700)};

        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionalRepository.sumByCategoryAndUserAndTypeAndDataBetween(
                eq(user), eq(TransactionType.EXPENSE), any(LocalDate.class), any(LocalDate.class)))
                .thenReturn(List.of(row1, row2));
        when(budgetRepository.findByCategoryAndYearMonth(any(), eq(month))).thenReturn(Optional.empty());

        List<CategorySummaryDTO> result = service.getCategorySummary(user.getEmail(), month, TransactionType.EXPENSE);

        assertEquals(2, result.size());
        // total = 1000; cat1 = 30%, cat2 = 70%
        assertEquals(0, result.get(0).getPercentage().compareTo(BigDecimal.valueOf(30)));
        assertEquals(0, result.get(1).getPercentage().compareTo(BigDecimal.valueOf(70)));
    }

    @Test
    void shouldReturnEmptyListWhenNoExpensesInMonth() {
        when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(transactionalRepository.sumByCategoryAndUserAndTypeAndDataBetween(
                any(), any(), any(), any())).thenReturn(List.of());

        List<CategorySummaryDTO> result = service.getCategorySummary(user.getEmail(), month, TransactionType.EXPENSE);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFoundOnCategorySummary() {
        when(userRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () ->
                service.getCategorySummary("inexistente@email.com", month, TransactionType.EXPENSE)
        );
    }
}
