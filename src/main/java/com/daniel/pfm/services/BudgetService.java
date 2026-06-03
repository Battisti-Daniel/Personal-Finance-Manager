package com.daniel.pfm.services;

import com.daniel.pfm.dtos.Budget.BudgetRequestDTO;
import com.daniel.pfm.dtos.Budget.BudgetResponseDTO;
import com.daniel.pfm.exceptions.BudgetAlreadyExistsException;
import com.daniel.pfm.exceptions.BudgetNotFoundException;
import com.daniel.pfm.exceptions.CategoryDoesNotExistsException;
import com.daniel.pfm.models.Budget;
import com.daniel.pfm.models.Category;
import com.daniel.pfm.models.User;
import com.daniel.pfm.repository.BudgetRepository;
import com.daniel.pfm.repository.CategoryRepository;
import com.daniel.pfm.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Transactional
    public BudgetResponseDTO create(BudgetRequestDTO dto, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));

        Category category = categoryRepository.findByIdAndUser(dto.getCategoryId(), user)
                .orElseThrow(CategoryDoesNotExistsException::new);

        if (budgetRepository.existsByCategoryAndYearMonth(category, dto.getYearMonth())) {
            throw new BudgetAlreadyExistsException();
        }

        Budget budget = new Budget(category, dto.getYearMonth(), dto.getAmount());
        return new BudgetResponseDTO(budgetRepository.save(budget));
    }

    public List<BudgetResponseDTO> findAllByMonth(String email, String yearMonth) {

        return budgetRepository
                .findAllByCategory_User_EmailAndYearMonth(email, yearMonth)
                .stream()
                .map(BudgetResponseDTO::new)
                .toList();
    }

    @Transactional
    public BudgetResponseDTO update(UUID id, BigDecimal amount, String email) {

        Budget budget = budgetRepository.findByIdAndCategory_User_Email(id, email)
                .orElseThrow(BudgetNotFoundException::new);

        budget.setAmount(amount);
        return new BudgetResponseDTO(budgetRepository.save(budget));
    }

    @Transactional
    public void delete(UUID id, String email) {

        Budget budget = budgetRepository.findByIdAndCategory_User_Email(id, email)
                .orElseThrow(BudgetNotFoundException::new);

        budgetRepository.delete(budget);
    }
}
